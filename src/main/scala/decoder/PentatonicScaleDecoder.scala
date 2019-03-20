package decoder

import akka.NotUsed
import akka.stream.scaladsl.Flow
import io.artos.activities.MerkleTreeCreatedActivity
import music._

class PentatonicScaleDecoder(tonic: Tonic) extends MerkleRootDecoder {

  // TODO: Define high-level patterns
  // TODO: Reduce the steps (add context)
  override def decode: Flow[MerkleTreeCreatedActivity, Note, NotUsed] = Flow[MerkleTreeCreatedActivity]
    .map(_.merkleRoot)
    .map(_.drop(2))
    .mapConcat[String](_.sliding(3, 3).toList)
    .map(_.toList)
    .mapConcat {
      case noteStr :: rhythmStr :: directionStr :: Nil =>
        val direction = directionMap(directionStr)
        val rhythm = rhythmMap(rhythmStr)

        val noteFactory = notesMap(_: Char)(tonic)(direction, rhythm)

        rhythm match {
          case DDouble   => noteFactory(noteStr) :: QuickStop :: noteFactory(directionStr) :: Nil
          case Quadruple => noteFactory(noteStr) :: QuickStop :: noteFactory(rhythmStr) :: QuickStop :: noteFactory(directionStr) :: QuickStop :: noteFactory(noteStr) :: Nil
          case _         => noteFactory(noteStr) :: Nil
        }

      case _ => Nil // TODO: tonic
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

    '1' -> (_.tonic),
    '2' -> (_.`3m`),
    '3' -> (_.`4`),
    '4' -> (_.`5j`),
    '5' -> (_.`7m`),

    '6' -> octaveUp(_.tonic),
    '7' -> octaveUp(_.`2m`),
    '8' -> octaveUp(_.`4`),
    '9' -> octaveUp(_.`5j`),
    'a' -> octaveUp(_.`7m`),

    'b' -> octaveDown(_.tonic),
    'c' -> octaveDown(_.`2m`),
    'd' -> octaveDown(_.`4`),
    'e' -> octaveDown(_.`5j`),
    'f' -> octaveDown(_.`7m`),
  )

  private def octaveDown(gap: Tonic => (Direction, Rhythm) => Note)(n: Tonic) =
    (d: Direction, r: Rhythm) => gap(n.octave(Down, r).toTonic)(d, r)

  private def octaveUp(gap: Tonic => (Direction, Rhythm) => Note)(n: Tonic) =
    (d: Direction, r: Rhythm) => gap(n.octave(Up, r).toTonic)(d, r)
}