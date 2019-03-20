import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import decoder.BasicChromaticScaleDecoder
import io.artos.activities.{MerkleTreeCreatedActivity, TraceData}
import music._
import stream.MerkleRootSource
import websocket.NoteService

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
    println("Playing: " + note)
    beatMaker.play(tempo)(note)
  }

  (new MerkleRootSource).source via flow runWith sink

  ///// WebSocket \\\\\ TODO refactoring

  val noteService = new NoteService(source via flow)

  val serverSource = Http().bind(interface = "localhost", port = 8080)

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)

      connection handleWithSyncHandler noteService.requestHandler
    }).run()
}
