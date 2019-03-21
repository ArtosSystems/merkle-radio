package music

import akka.NotUsed
import akka.actor.ActorRef
import akka.stream.{OverflowStrategy, SourceShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Source, Zip}
import decoder.{AdvancedFillerScaleDecoder, RhythmMaker}
import io.artos.activities.MerkleTreeCreatedActivity
import stream.MerkleRootSource

import scala.concurrent.ExecutionContext

class MusicSource(musicParamsActor: ActorRef)(implicit ec: ExecutionContext) {
  val source: Source[Note, NotUsed] = Source.fromGraph(GraphDSL.create() { implicit builder =>
    import akka.stream.scaladsl.GraphDSL.Implicits._

    val in = (new MerkleRootSource).source

    val notesFlow = new AdvancedFillerScaleDecoder(musicParamsActor).decode
    val rhythmFlow = RhythmMaker.produceRhythm

    val drop0x = Flow[MerkleTreeCreatedActivity].map(_.merkleRoot.drop(2))
    val buffer = Flow[String].buffer(1, OverflowStrategy.dropTail)
    val toChar = Flow[String].mapConcat(_.toList)
    val applyRhythm = Flow[(Rhythm, Rhythm => Note)].map { case (r, n) => n(r) }
    val addStops = builder.add(Flow[Note].mapConcat(_ :: QuickStop :: Nil))

    val bcast = builder.add(Broadcast[String](2))
    val zip = builder.add(Zip[Rhythm, Rhythm => Note]())

    in ~> drop0x ~> bcast.in
                    bcast.out(0) ~> notesFlow                      ~> zip.in1
                    bcast.out(1) ~> buffer ~> toChar ~> rhythmFlow ~> zip.in0
                                                                      zip.out ~> applyRhythm ~> addStops

    SourceShape(addStops.out)
  })
}
