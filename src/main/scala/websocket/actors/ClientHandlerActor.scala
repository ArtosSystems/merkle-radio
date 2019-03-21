package websocket.actors


import akka.NotUsed
import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.pattern.ask
import akka.stream.scaladsl.{BroadcastHub, Flow, GraphDSL, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import music.{BeatMaker, MusicSource, Note}
import websocket.actors.MusicParamsActor.{BpmValue, ChangeBpm, ChangeTonic, GetBpm}
import websocket.actors.ClientHandlerActor.GetWebsocketFlow

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

class ClientHandlerActor(beatMaker: BeatMaker) extends Actor with StrictLogging {
  implicit val am: ActorMaterializer = ActorMaterializer()
  implicit val exCtx: ExecutionContextExecutor = context.dispatcher
  implicit val askTimeout: Timeout = Timeout(30.seconds)

  private val bufferSize = 1024

  private val musicParamsActor = context.actorOf(MusicParamsActor.props)

  private val musicSource = new MusicSource(musicParamsActor).source

  private val (down, publisher) = Source
    .actorRef[String](bufferSize, OverflowStrategy.fail)
    .toMat(BroadcastHub.sink(bufferSize))(Keep.both)
    .run()

  musicSource
    .mapAsync(1)(n => (musicParamsActor ? GetBpm).map{ case BpmValue(bpm) => beatMaker.play(bpm)(n) })
    .runForeach(down.!)
    .recover {
      case e => println(s"Error: ${e.getMessage}")
    }

  context.system.scheduler.schedule(10.seconds, 10.seconds, down, "keep-alive")

  Sink.ignore.runWith(publisher) // so we don't buffer incoming messages when no client is listening

  private val bpmRegex = "bpm=([0-9]+)".r
  private val tonicRegex = "tonic=([0-9]+)".r

  override def receive: PartialFunction[Any, Unit] = {
    case GetWebsocketFlow =>
      val flow = Flow.fromGraph(GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._

        // only works with TextMessage. Extract the body and sends it to self
        val textMsgFlow = b.add(Flow[Message]
          .mapAsync(1) {
            case tm: TextMessage => tm.textStream.runFold("")(_ + _)
            case bm: BinaryMessage =>
              // consume the stream
              bm.dataStream.runWith(Sink.ignore)
              Future.failed(new Exception("Binary messages are ignored."))
          })

        val pubSrc = b.add(publisher.map(TextMessage(_)))

        textMsgFlow ~> Sink.foreach[String](self ! _)
        FlowShape(textMsgFlow.in, pubSrc.out)
      })

      sender ! flow

    // sends activities down the websocket
    case s: String =>
      println(s"received: $s")
      s match {
        case bpmRegex(bpm)     => musicParamsActor ! ChangeBpm(bpm.toInt)
        case tonicRegex(tonic) => musicParamsActor ! ChangeTonic(tonic.toInt)
        case _                 => down ! s
      }
  }
}

object ClientHandlerActor {
  def props(beatMaker: BeatMaker) = Props(new ClientHandlerActor(beatMaker))

  case object GetWebsocketFlow
}
