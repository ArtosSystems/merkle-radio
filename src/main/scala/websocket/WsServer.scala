package websocket

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.pattern.ask
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.Timeout
import io.circe.syntax._
import music.{BeatMaker, Note}
import websocket.WsServer._
import websocket.actors.BpmActor.{Bpm, ChangeBpm}
import websocket.actors.MasterActor
import websocket.actors.MasterActor.{GetBpm, PingServer}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

class WsServer private(beatMaker: BeatMaker, noteSource: Source[Note, NotUsed])(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {

  private val masterActor = system.actorOf(MasterActor.props)

  system.scheduler.schedule(1.minute, 1.minute)(masterActor ! PingServer)

  val requestHandlerAsync: HttpRequest => Future[HttpResponse] = {

    case req @ HttpRequest(GET, Uri.Path("/note-stream"), _, _, _)  => Future{ handleWsRequest(req, noteStream(noteSource, beatMaker, masterActor)) }

    case req @ HttpRequest(GET, Uri.Path("/change-bpm"), _, _, _)   => Future{ handleWsRequest(req, changeBpm(masterActor)) }

    case r: HttpRequest => Future{
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      HttpResponse(404, entity = "Unknown resource!")
    }
  }

  private def handleWsRequest(req: HttpRequest, handler: Flow[Message, TextMessage, NotUsed]): HttpResponse = {
    req.header[UpgradeToWebSocket] match {
      case Some(upgrade) => upgrade.handleMessages(handler)
      case None          => HttpResponse(400, entity = "Not a valid websocket request!")
    }
  }
}



object WsServer{

  implicit val askTimeout: Timeout = Timeout(30.seconds)

  def apply(beatMaker: BeatMaker, noteSource: Source[Note, NotUsed])
           (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) = new WsServer(beatMaker, noteSource)


  def noteStream(noteSource: Source[Note, NotUsed], beatMaker: BeatMaker, masterActor: ActorRef)(implicit mat: Materializer, ec: ExecutionContext): Flow[Message, TextMessage, NotUsed] = {

    def playingFlow(beatMaker: BeatMaker): Flow[Note, Note, NotUsed] = Flow[Note]
      .mapAsync(1){ n =>
        (masterActor ? GetBpm)
          .map { case Bpm(value) => println("GOT BPM: " + value); value }
          .map(bpm => (n, bpm))
      }
      .map { case (note, beats) =>
        println("Playing: " + note)
        beatMaker.play(beats)(note)
        note
      }


    val source =
      noteSource
        .via(playingFlow(beatMaker))
        .map(_.asJson.noSpaces)

    Flow[Message]
      .mapConcat {
        case _ : TextMessage    => TextMessage(source) :: Nil
        case bm: BinaryMessage  =>
          // ignore binary messages but drain content to avoid the stream being clogged
          bm.dataStream.runWith(Sink.ignore)
          Nil
      }
  }

  def changeBpm(masterActor: ActorRef)(implicit mat: Materializer, ec: ExecutionContext): Flow[Message, TextMessage, NotUsed] = {

    Flow[Message]
      .mapAsync(1) {
        case tm : TextMessage   =>
          Future{masterActor ! ChangeBpm(tm.getStrictText.toInt)}
            .flatMap{_ =>
              (masterActor ? GetBpm).map{ case Bpm(value) => TextMessage(s"BPM changed to $value")  }
            }
          //tm :: Nil

        case bm: BinaryMessage  =>
          // ignore binary messages but drain content to avoid the stream being clogged
          bm.dataStream.runWith(Sink.ignore)
          //Nil
          null
      }
  }
}

