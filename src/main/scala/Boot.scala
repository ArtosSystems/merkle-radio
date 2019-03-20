import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import decoder.PentatonicScaleDecoder
import io.artos.activities.{MerkleTreeCreatedActivity, TraceData}
import websocket.{ServiceHandlers, WsServer}
import music._
import stream.MerkleRootSource

import scala.concurrent.Future

object Boot extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val beatMaker = BeatMaker()

  val tempo = 170

  val tonic = Tonic(440)

  private val root = MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef", 2, 0)

  val source = Source(List(root))
  val flow = new PentatonicScaleDecoder(tonic).decode
  val sink = Sink.foreach[Note] { note =>
    println("Playing: " + note)
    beatMaker.play(tempo)(note)
  }

  (new MerkleRootSource).source via flow runWith sink

  ///// WebSocket \\\\\ TODO refactoring

  val wsServer = WsServer(new ServiceHandlers(source via flow))

  val serverSource = Http().bind(interface = "localhost", port = 8080)

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)

      connection handleWithSyncHandler wsServer.requestHandler
    }).run()
}
