import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import decoder.BasicChromaticScaleDecoder
import io.artos.activities.{MerkleTreeCreatedActivity, TraceData}
import music.{BeatMaker, Black, Note}
import websocket.{ServiceHandlers, WsServer}

import scala.concurrent.Future

object Boot extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val beatMaker = BeatMaker()

  val tempo = 100

  val tonic = Note(440, Black)

  private val root = MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef", 2, 0)

  val source = Source(List(root))
  val flow = new BasicChromaticScaleDecoder(tonic).decode
  val sink = Sink.foreach[Note] { note =>
//    println(note)
    beatMaker.play(tempo)(note)
  }

  source via flow runWith sink foreach (_ => beatMaker.stop())



  ///// WebSocket \\\\\ TODO refactoring

  val wsServer = WsServer(new ServiceHandlers(source via flow))

  val serverSource = Http().bind(interface = "localhost", port = 8080)

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)

      connection handleWithSyncHandler wsServer.requestHandler
    }).run()
}
