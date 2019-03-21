package decoder

import akka.NotUsed
import akka.actor.ActorRef
import akka.pattern.ask
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Unzip, Zip}
import music._
import websocket.actors.MusicParamsActor.{GetTonic, TonicValue}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class AdvancedFillerScaleDecoder(musicParamsActor: ActorRef)(implicit ec: ExecutionContext) extends MerkleRootDecoder {

  private val maxGap = 5 // more than  a 4th of difference

  override def decode: Flow[String, Rhythm => Note, NotUsed] = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val in = b.add(Flow[String])
    val zip = b.add(Zip[List[Char], List[Char]]())

    val duplicate = Flow[String].map(s => (s, s))
    val dropFirst = Flow[String].map(_.drop(2))
    val dropLast = Flow[String].map(_.dropRight(2))
    val extractTriplets = Flow[String].mapConcat[String](_.sliding(2, 2).toList).map(_.toList)
    val offset = b.add(Unzip[String, String]())
    val out = b.add(noteFactory)

    in ~> duplicate ~> offset.in
                       offset.out0 ~> dropFirst ~> extractTriplets ~> zip.in1
                       offset.out1 ~> dropLast  ~> extractTriplets ~> zip.in0
                                                                      zip.out ~> out

    FlowShape(in.in, out.out)
  })

  private def noteFactory: Flow[(List[Char], List[Char]), Rhythm => Note, NotUsed] = Flow[(List[Char], List[Char])]
    .mapAsync(1) {
      case (t1, t2) =>
        (musicParamsActor ? GetTonic)(3.seconds)
          .mapTo[TonicValue]
          .map((t1, t2, _))
    }
    .mapConcat {
      case (lastNoteStr :: _ :: Nil, noteStr :: directionStr :: Nil, TonicValue(tonicInt)) =>
        val tonic = Tonic(tonicInt)
        val direction = directionMap(directionStr)

        val noteFactory = majorScale(_: Char)(tonic)(direction)

        val target = noteFactory(noteStr)
        val lastNote = noteFactory(lastNoteStr)

        (target, lastNote) match {
          case (h @ Height(_, gap), lh @ Height(_, lastGap)) if Math.abs(gap - lastGap) == 4 =>
            lh.toTonic.`3m`(direction) :: lh.toTonic.`4`(direction) :: lh.toTonic.`5j`(direction) :: h.toTonic.tonic(direction) :: Nil

          case (h @ Height(_, gap), Height(_, lastGap)) if Math.abs(gap - lastGap) > maxGap =>
            fillUpGap(direction, lastNoteStr, noteStr, tonic) :+ h

          case (h: Height, _) =>
            h :: Nil
        }

      case (_, _, TonicValue(tonicInt)) => Tonic(tonicInt).tonic(Up) :: Nil
    }
    .map(_.toNote)

  private def fillUpGap(direction: Direction, lastNoteStr: Char, targetNoteStr: Char, tonic: Tonic): List[Height] = {
    val lastNoteIndex = keys.indexOf(lastNoteStr)
    val targetNoteIndex = keys.indexOf(targetNoteStr)

    val notesIndexed = majorScale.toIndexedSeq.sortBy(_._1)
    val ints = List.range(lastNoteIndex, targetNoteIndex)
    val fillers = ints.map(notesIndexed).map(_._2(tonic)(direction))
    fillers
  }

  private val directionMap: Map[Char, Direction] = Map(
    '0' -> Down,
    '1' -> Up,
    '2' -> Down,
    '3' -> Up,
    '4' -> Down,
    '5' -> Up,
    '6' -> Down,
    '7' -> Up,
    '8' -> Down,
    '9' -> Up,
    'a' -> Down,
    'b' -> Up,
    'c' -> Down,
    'd' -> Up,
    'e' -> Down,
    'f' -> Up,
  )

  private val majorScale: Map[Char, Tonic => Direction => Height] = Map(
    '0' -> octaveDown(_.tonic),
    '1' -> octaveDown(_.`2M`),
    '2' -> octaveDown(_.`3M`),
    '3' -> octaveDown(_.`4`),
    '4' -> (_.tonic),
    '5' -> (_.`2M`),
    '6' -> (_.`3M`),
    '7' -> (_.`4`),
    '8' -> (_.`5j`),
    '9' -> (_.`6M`),
    'a' -> (_.`7M`),
    'b' -> octaveUp(_.tonic),
    'c' -> octaveUp(_.`2M`),
    'd' -> octaveUp(_.`3M`),
    'e' -> octaveUp(_.`4`),
    'f' -> (_.tonic),
  )

  private val minorPentatonicMap: Map[Char, Tonic => Direction => Height] = Map(
    '0' -> (_.tonic),

    '1' -> octaveDown(_.tonic),
    '2' -> octaveDown(_.`2m`),
    '3' -> octaveDown(_.`4`),
    '4' -> octaveDown(_.`5j`),
    '5' -> octaveDown(_.`7m`),

    '6' -> (_.tonic),
    '7' -> (_.`3m`),
    '8' -> (_.`4`),
    '9' -> (_.`5j`),
    'a' -> (_.`7m`),

    'b' -> octaveUp(_.tonic),
    'c' -> octaveUp(_.`2m`),
    'd' -> octaveUp(_.`4`),
    'e' -> octaveUp(_.`5j`),
    'f' -> octaveUp(_.`7m`),
  )

  private val keys = majorScale.keys.toIndexedSeq.sorted

  private def octaveDown(gap: Tonic => Direction => Height)(n: Tonic) =
    (d: Direction) => gap(n.octave(Down).toTonic)(d)

  private def octaveUp(gap: Tonic => Direction => Height)(n: Tonic) =
    (d: Direction) => gap(n.octave(Up).toTonic)(d)
}