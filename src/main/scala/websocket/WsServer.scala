package websocket

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import music.{BeatMaker, Note, Tonic}
import websocket.actors.MasterActor
import websocket.actors.MasterActor.{GetBpm, PingServer, Start}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import scala.concurrent.duration._
import akka.http.scaladsl.unmarshalling.Unmarshal
import decoder.PentatonicScaleDecoder
import stream.MerkleRootSource
import WsServer._
import akka.util.Timeout
import websocket.actors.BpmActor.{Bpm, ChangeBpm}
import akka.pattern.ask

class WsServer private(beatMaker: BeatMaker)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {


  implicit val askTimeout: Timeout = Timeout(30.seconds)

  private val tonic = Tonic(440)

  private val noteSource = (new MerkleRootSource).source via new PentatonicScaleDecoder(tonic).decode

  private val masterActor = system.actorOf(MasterActor.props(beatMaker, noteSource))

  system.scheduler.schedule(1.minute, 1.minute)(masterActor ! PingServer)

  masterActor ! Start

  val requestHandlerAsync: HttpRequest => Future[HttpResponse] = {

    case req @ HttpRequest(GET, Uri.Path("/note-stream"), _, _, _)  => Future {
      handleWsRequest(req, noteStream(noteSource, beatMaker, masterActor))
    }

    case req @ HttpRequest(GET, Uri.Path("/change-bpm"), _, _, _)  => Future{ handleWsRequest(req, changeBpm(masterActor)) }

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
  import akka.NotUsed
  import akka.http.scaladsl.model.ws._
  import akka.stream.Materializer
  import akka.stream.scaladsl.{Flow, Sink, Source}
  import io.circe.syntax._
  import music.Note


  def apply(beatMaker: BeatMaker)
           (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) = new WsServer(beatMaker)


  def noteStream(noteSource: Source[Note, NotUsed], beatMaker: BeatMaker, masterActor: ActorRef)(implicit mat: Materializer, ec: ExecutionContext): Flow[Message, TextMessage, NotUsed] = {

    implicit val askTimeout: Timeout = Timeout(30.seconds)

    def playingFlow(beatMaker: BeatMaker): Flow[Note, Note, NotUsed] = Flow[Note]
      .mapAsync(1){ n =>
        (masterActor ? GetBpm)
          .map { case Bpm(value) => {println(value); value} }
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

    implicit val askTimeout: Timeout = Timeout(30.seconds)

    Flow[Message]
      .mapAsync(1) {
        case tm : TextMessage   =>

          Future{masterActor ! ChangeBpm(tm.getStrictText.toInt)}
            .flatMap{_ =>
              (masterActor ? GetBpm).map{ case Bpm(value) => TextMessage(s"BPM changed to ${value}")  }
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

