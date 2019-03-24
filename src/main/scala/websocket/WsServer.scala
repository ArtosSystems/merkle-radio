package websocket

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.ws.{TextMessage, _}
import akka.http.scaladsl.model.ws.TextMessage._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.pattern.ask
import akka.stream._
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source}
import akka.util.Timeout
import io.circe.syntax._
import music.{BeatMaker, Note}
import websocket.WsServer._
import websocket.actors.BpmActor.{Bpm, ChangeBpm}
import websocket.actors.MasterActor.{GetBpm, PingServer}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

class WsServer private(beatMaker: BeatMaker, noteSource: Source[Note, NotUsed], masterActor: ActorRef)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {

  private val out = outboundStream(noteSource, masterActor, beatMaker)
  private val in = inboundStream(masterActor)

  system.scheduler.schedule(1.minute, 1.minute)(masterActor ! PingServer)

  val requestHandlerAsync: HttpRequest => Future[HttpResponse] = {

    case req @ HttpRequest(GET, Uri.Path("/note-stream"), _, _, _)  => Future{ handleWsRequest(req, in, out) }

    case r: HttpRequest => Future{
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      HttpResponse(404, entity = "Unknown resource!")
    }
  }

  private def handleWsRequest(req: HttpRequest, inSink: Sink[Message, NotUsed], outSource: Source[Message, NotUsed]): HttpResponse = {

    req.header[UpgradeToWebSocket] match {
      case Some(upgrade) => upgrade.handleMessagesWithSinkSource(inSink, outSource) //upgrade.handleMessages(handler)
      case None          => HttpResponse(400, entity = "Not a valid websocket request!")
    }
  }
}



object WsServer{

  implicit val askTimeout: Timeout = Timeout(30.seconds)


  def apply(beatMaker: BeatMaker, noteSource: Source[Note, NotUsed], masterActor: ActorRef)
           (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) = new WsServer(beatMaker, noteSource, masterActor)


  def inboundStream(masterActor: ActorRef)(implicit mat: Materializer, ec: ExecutionContext): Sink[Message, NotUsed] = {
    val inSink = Sink.fromGraph(GraphDSL.create() { implicit b =>

      import GraphDSL.Implicits._

      val messageReader: FlowShape[Message, String] = b.add(
        Flow[Message]
          .mapAsync(1) {
            case tm: TextMessage    => tm.textStream.runFold("")(_ + _).map(Some(_))
            // ignore binary messages but drain content to avoid the stream being clogged
            case bm: BinaryMessage  => bm.dataStream.runWith(Sink.ignore).map(_ => None)
          }.collect{ case Some(msg) => msg }
      )

      val bpmRegex = "bpm=([0-9]+)".r
      val bpmExtractor = Flow[Message].collect{ case Strict(bpmRegex(bpm)) => bpm.toInt }

      val passThrough = Flow[String].filterNot{ bpmRegex.findFirstIn(_).isDefined }

      val bpmChanger =  Sink.foreach[Int](bpm => {
        println("changing BPM to -> " + bpm)
        masterActor ! ChangeBpm(bpm)
      })

      val debugSink = Sink.foreach[String](msg => println(s"RECEIVED: $msg, will be ignored..." ))

      val broadcast = b.add(Broadcast[Message](2))

      // doesn't make much sense, but useful for practicing...
      broadcast.out(0) ~> bpmExtractor  ~> bpmChanger
      broadcast.out(1) ~> messageReader ~> passThrough  ~> debugSink

      SinkShape(broadcast.in)
    })

    inSink
  }

  def outboundStream(musicSource: Source[Note, NotUsed], masterActor: ActorRef, beatMaker: BeatMaker)(implicit mat: Materializer, ec: ExecutionContext): Source[Strict, NotUsed] = {

    val outSource = Source.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val bpmQuery = Flow[Note].mapAsync(1){note => (masterActor ? GetBpm).map { case Bpm(bpm) => (note, bpm) }}

      val play = Flow[(Note, Int)].map{ case (note, beats) =>
        println("Playing: " + note)
        beatMaker.play(beats)(note)
        note
      }

      val pushToClient = b.add(Flow[String].map(TextMessage(_)))

      val serialize = Flow[Note].map(_.asJson.noSpaces)

      musicSource ~> bpmQuery ~> play ~> serialize ~> pushToClient

      SourceShape(pushToClient.out)

    })

    outSource
  }
}

