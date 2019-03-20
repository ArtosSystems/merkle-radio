import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source, Zip}
import akka.stream.{ActorMaterializer, SourceShape}
import decoder.{PentatonicScaleDecoder, RhythmMaker}
import io.artos.activities.MerkleTreeCreatedActivity
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

  val tonic = Tonic(440)

  val source: Source[Note, NotUsed] = Source.fromGraph(GraphDSL.create() { implicit builder =>
    import akka.stream.scaladsl.GraphDSL.Implicits._

    val in = (new MerkleRootSource).source

    val notesFlow = new PentatonicScaleDecoder(tonic).decode
    val rhythmFlow = RhythmMaker.produceRhythm

    val drop0x = Flow[MerkleTreeCreatedActivity].map(_.merkleRoot.drop(2))
    val toChar = Flow[String].mapConcat(_.toList)
    val applyRhythm = Flow[(Rhythm, Rhythm => Note)].map { case (r, n) => n(r) }
    val addStops = builder.add(Flow[Note].mapConcat(_ :: QuickStop :: Nil))

    val bcast = builder.add(Broadcast[String](2))
    val zip = builder.add(Zip[Rhythm, Rhythm => Note]())

    in ~> drop0x ~> bcast.in
                    bcast.out(0) ~> notesFlow            ~> zip.in1
                    bcast.out(1) ~> toChar ~> rhythmFlow ~> zip.in0
                                                            zip.out ~> applyRhythm ~> addStops

    SourceShape(addStops.out)
  })

  val player = Sink.foreach[Note] { note =>
    println("Playing: " + note)
    beatMaker.play(tempo)(note)
  }

  source.runWith(player).recover {
    case e => println("Error: " + e)
  }

  ///// WebSocket \\\\\ TODO refactoring

  val noteService = new NoteService(source)

  val serverSource = Http().bind(interface = "localhost", port = 8080)

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)

      connection handleWithSyncHandler noteService.requestHandler
    }).run()
}
