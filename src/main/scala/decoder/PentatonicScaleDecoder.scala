package decoder

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Unzip, Zip}
import music._

class PentatonicScaleDecoder(tonic: Tonic) extends MerkleRootDecoder {

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
    .mapConcat {
      case (lastNoteStr :: _ :: Nil, noteStr :: directionStr :: Nil) =>
        val direction = directionMap(directionStr)

        val noteFactory = notesMap(_: Char)(tonic)(direction)

        val target = noteFactory(noteStr)
        val lastNote = noteFactory(lastNoteStr)

        (target, lastNote) match {
          case (h @ Height(_, gap), lh @ Height(_, lastGap)) if Math.abs(gap - lastGap) == 4 =>
            lh.toTonic.`3m`(direction) :: lh.toTonic.`4`(direction) :: lh.toTonic.`5j`(direction) :: h.toTonic.tonic(direction) :: Nil

          case (h @ Height(_, gap), Height(_, lastGap)) if Math.abs(gap - lastGap) > maxGap =>
            fillUpGap(direction, lastNoteStr, noteStr) :+ h

          case (h: Height, _) =>
            h :: Nil
        }

      case _ => tonic.tonic(Up) :: Nil
    }
    .map(_.toNote)

  private def fillUpGap(direction: Direction, lastNoteStr: Char, targetNoteStr: Char): List[Height] = {
    val lastNoteIndex = keys.indexOf(lastNoteStr)
    val targetNoteIndex = keys.indexOf(targetNoteStr)

    val notesIndexed = notesMap.toIndexedSeq.sortBy(_._1)
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

  private val notesMap: Map[Char, Tonic => Direction => Height] = Map(
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

  private val keys = notesMap.keys.toIndexedSeq.sorted

  private def octaveDown(gap: Tonic => Direction => Height)(n: Tonic) =
    (d: Direction) => gap(n.octave(Down).toTonic)(d)

  private def octaveUp(gap: Tonic => Direction => Height)(n: Tonic) =
    (d: Direction) => gap(n.octave(Up).toTonic)(d)
}