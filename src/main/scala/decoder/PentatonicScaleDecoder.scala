package decoder

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Unzip, Zip}
import io.artos.activities.MerkleTreeCreatedActivity
import music._

import scala.util.Random

class PentatonicScaleDecoder(tonic: Tonic) extends MerkleRootDecoder {

  private val maxGap = 7 // more than  a 5th of difference

  // TODO: Define high-level patterns
  override def decode: Flow[MerkleTreeCreatedActivity, Note, NotUsed] = prepare
    .mapConcat {
      case (lastNoteStr :: lastRhythmStr :: lastDirectionStr :: Nil, noteStr :: rhythmStr :: directionStr :: Nil) =>
        val direction = directionMap(directionStr)
        val rhythm = rhythmMap(rhythmStr)

        val noteFactory = notesMap(_: Char)(tonic)(direction, rhythm)

        val target = noteFactory(noteStr)
        val lastNote = noteFactory(lastNoteStr)

        (target, lastNote) match {
          case (r: Rest, _) =>
            r :: Nil

          case (q @ QuickStop, _) =>
            q :: Nil

          case (s @ Sound(_, _, gap), ls @ Sound(_, _, lastGap)) if Math.abs(gap - lastGap) == 4 =>
            ls :: ls.toTonic.`3m`(direction, Quadruple) :: ls.toTonic.`4`(direction, Quadruple) :: ls.toTonic.`5j`(direction, Quadruple) :: s :: Nil

          case (s @ Sound(_, _, gap), ls @ Sound(_, _, lastGap)) if Math.abs(gap - lastGap) > maxGap =>
            ls +: fillUpGap(direction, lastNoteStr, noteStr) :+ s

          case (s @ Sound(_, _, gap), _) =>
            s :: Nil
        }

      case _ => tonic.tonic(Up, White) :: Nil
    }

  private def fillUpGap(direction: Direction, lastNoteStr: Char, targetNoteStr: Char, acc: List[Note] = List(QuickStop)): List[Note] = {
    val keys = notesMap.keys
    val lastIntervalIndex = keys.toIndexedSeq.indexOf(lastNoteStr)
    val targetIntervalIndex = keys.toIndexedSeq.indexOf(targetNoteStr)

    val diff = targetIntervalIndex - lastIntervalIndex
    if (math.abs(diff) > 3) {
      val nextGapFiller =
        if (diff > 0) lastIntervalIndex + Random.nextInt(math.abs(diff))
        else          lastIntervalIndex - Random.nextInt(math.abs(diff))

      val nextFillerInterval = notesMap.toSeq(nextGapFiller)
      val nextFillerNote = nextFillerInterval._2(tonic)(direction, DDouble)

      fillUpGap(direction, nextFillerInterval._1, targetNoteStr, acc ++ (nextFillerNote :: QuickStop :: Nil))
    }
    else {
      acc
    }
  }

  private def prepare = Flow.fromGraph(GraphDSL.create() { implicit b ⇒
    import GraphDSL.Implicits._

    val in = b.add(Flow[MerkleTreeCreatedActivity])
    val zip = b.add(Zip[List[Char], List[Char]]())

    val drop0x = Flow[MerkleTreeCreatedActivity].map {
      m =>
        println(m)
        m.merkleRoot.drop(2).toLowerCase
    }
    val duplicate = Flow[String].map(s => (s, s))
    val dropFirst = Flow[String].map(_.drop(3))
    val dropLast = Flow[String].map(_.dropRight(3))
    val extractTriplets = Flow[String].mapConcat[String](_.sliding(3, 3).toList).map(_.toList)
    val offset = b.add(Unzip[String, String]())

    in ~> drop0x ~> duplicate ~> offset.in
                                 offset.out0 ~> dropFirst ~> extractTriplets ~> zip.in1
                                 offset.out1 ~> dropLast  ~> extractTriplets ~> zip.in0

    FlowShape(in.in, zip.out)
  })

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

  private val rhythmMap: Map[Char, Rhythm] = Map(
    '0' -> White,
    '4' -> White,
    '8' -> Black,
    'c' -> Black,
    '1' -> Black,
    '5' -> Black,
    '9' -> Black,
    'd' -> Black,
    '2' -> DDouble,
    '6' -> DDouble,
    'a' -> DDouble,
    'e' -> DDouble,
    '3' -> DDouble,
    '7' -> DDouble,
    'b' -> Quadruple,
    'f' -> Quadruple,
  )

  private val notesMap: Map[Char, Tonic => (Direction, Rhythm) => Note] = Map(
    '0' -> (n => (_, r) => n.rest(r)),

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

  private def octaveDown(gap: Tonic => (Direction, Rhythm) => Note)(n: Tonic) =
    (d: Direction, r: Rhythm) => gap(n.octave(Down, r).toTonic)(d, r)

  private def octaveUp(gap: Tonic => (Direction, Rhythm) => Note)(n: Tonic) =
    (d: Direction, r: Rhythm) => gap(n.octave(Up, r).toTonic)(d, r)
}