package websocket

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.ws.{TextMessage, _}
import akka.http.scaladsl.model.ws.TextMessage._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.pattern.ask
import akka.stream._
import akka.stream.scaladsl.{Flow, GraphDSL, Sink, Source}
import akka.util.Timeout
import io.circe.syntax._
import music.{BeatMaker, Note, Tonic}
import websocket.WsServer._
import websocket.actors.BpmActor.{Bpm, ChangeBpm}
import websocket.actors.MasterActor
import websocket.actors.MasterActor.{GetBpm, PingServer}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

class WsServer private(beatMaker: BeatMaker, noteSource: Source[Note, NotUsed], tonic: Tonic)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {

  private val masterActor = system.actorOf(MasterActor.props)

  system.scheduler.schedule(1.minute, 1.minute)(masterActor ! PingServer)

  val requestHandlerAsync: HttpRequest => Future[HttpResponse] = {

    case req @ HttpRequest(GET, Uri.Path("/note-stream"), _, _, _)  => {

      val (in, out) = noteStream(noteSource, beatMaker, masterActor)
      Future{ handleWsRequest2(req, in, out) }
    }

    case r: HttpRequest => Future{
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      HttpResponse(404, entity = "Unknown resource!")
    }
  }

//  private def handleWsRequest(req: HttpRequest, handler: Flow[Message, TextMessage, NotUsed]): HttpResponse = {
//    req.header[UpgradeToWebSocket] match {
//      case Some(upgrade) => upgrade.handleMessagesWith(inSink, outSource) //upgrade.handleMessages(handler)
//      case None          => HttpResponse(400, entity = "Not a valid websocket request!")
//    }
//  }

  //inSink: Graph[SinkShape[jm.ws.Message], _ <: Any], outSource: Graph[SourceShape[jm.ws.Message], _ <: Any]
  private def handleWsRequest2(req: HttpRequest, inSink: Sink[Message, NotUsed], outSource: Source[Message, NotUsed]): HttpResponse = {

    //(outSource to inSink).run()

    req.header[UpgradeToWebSocket] match {
      case Some(upgrade) => upgrade.handleMessagesWithSinkSource(inSink, outSource) //upgrade.handleMessages(handler)
      case None          => HttpResponse(400, entity = "Not a valid websocket request!")
    }
  }
}



object WsServer{

  implicit val askTimeout: Timeout = Timeout(30.seconds)


  def apply(beatMaker: BeatMaker, noteSource: Source[Note, NotUsed], tonic: Tonic)
           (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) = new WsServer(beatMaker, noteSource, tonic)


  def playingFlow(beatMaker: BeatMaker, masterActor: ActorRef)(implicit mat: Materializer, ec: ExecutionContext): Flow[Note, Note, NotUsed] =
    Flow[Note]
      .mapAsync(1){ n =>
        (masterActor ? GetBpm)
          .map { case Bpm(value) =>
            //println("HEY:::" + value)
            value }
          .map(bpm => (n, bpm))
      }
      .map { case (note, beats) =>
        println("Playing: " + note)
        beatMaker.play(beats)(note)
        note
      }


  def noteStream(noteSource: Source[Note, NotUsed], beatMaker: BeatMaker, masterActor: ActorRef)(implicit mat: Materializer, ec: ExecutionContext) = { //: Flow[Message, TextMessage, NotUsed] = {

    def source(master: ActorRef): Source[String, NotUsed] =
      noteSource
        .via(playingFlow(beatMaker, master))
        .map(_.asJson.noSpaces)

    val bpmRegex = "bpm=([0-9]+)".r

    val musicSource = source(masterActor)

    val inSink = Sink.fromGraph(GraphDSL.create() { implicit b =>

      import GraphDSL.Implicits._

      val flow: FlowShape[Message, Message] = b.add(Flow[Message].mapAsync(1) {
        case m @ Strict(bpmRegex(bpm)) => {

          println("RECEIVING BPM CHANGE" )
          masterActor ! ChangeBpm(bpm.toInt)
          m.textStream.runFold("")(_ + _).map(TextMessage.apply)
        }
        case tm : TextMessage   => {
          println("RECEIVING RUBBISH" )
          tm.textStream.runFold("")(_ + _).map(TextMessage.apply)
        }
        case bm: BinaryMessage  =>
          // ignore binary messages but drain content to avoid the stream being clogged
          bm.dataStream.runWith(Sink.ignore)
          //Nil
          null
      })

      flow ~> Sink.foreach[Message](m => println("RECEIVED: " + m))

      SinkShape(flow.in)
    })


    val outSource = Source.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val flow = b.add(Flow[String].map(TextMessage(_)))
      musicSource ~> flow

      SourceShape(flow.out)
    })

    (inSink, outSource)


//    Flow[Message]
//      .mapAsync(1) {
//        case m @ Strict(bpmRegex(bpm)) => {
//          masterActor ! ChangeBpm(bpm.toInt)
//          //TextMessage(source(masterActor) ++ m.textStream) :: Nil
//          (source(masterActor) ++ m.textStream).runFold("")(_ + _).map(TextMessage.apply)
//        }
//        case tm : TextMessage    => (source(masterActor) ++ tm.textStream).runFold("")(_ + _).map(TextMessage.apply) //TextMessage(source(masterActor) ++ tm.textStream) //:: Nil
//        case bm: BinaryMessage  =>
//          // ignore binary messages but drain content to avoid the stream being clogged
//          bm.dataStream.runWith(Sink.ignore)
//          //Nil
//        null
//
//        case _ => println("whatever"); null
//      }
  }
}

