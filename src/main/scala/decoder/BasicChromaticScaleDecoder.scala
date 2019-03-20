package decoder

import akka.NotUsed
import akka.stream.scaladsl.Flow
import music._

class BasicChromaticScaleDecoder(tonic: Tonic) extends MerkleRootDecoder {

  override def decode: Flow[String, Rhythm => Note, NotUsed] = Flow[String]
    .mapConcat[String](_.sliding(3, 3).toList)
    .map(_.toList)
    .mapConcat {
      case noteStr :: rhythmStr :: directionStr :: Nil =>
        val direction = directionMap(directionStr)
        val rhythm = rythmeMap(rhythmStr)

        val nextNote = notesMap(noteStr)(tonic)(direction)

        rhythm match {
          case DDouble   => nextNote :: nextNote :: Nil
          case Quadruple => nextNote :: nextNote :: nextNote :: nextNote :: Nil
          case _         => nextNote :: Nil
        }

      case _ => Nil
    }
    .map(_.toNote)

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

  private val rythmeMap: Map[Char, Rhythm] = Map(
    '0' -> White,
    '4' -> White,
    '8' -> White,
    'c' -> White,
    '1' -> Black,
    '5' -> Black,
    '9' -> Black,
    'd' -> Black,
    '2' -> DDouble,
    '6' -> DDouble,
    'a' -> DDouble,
    'e' -> DDouble,
    '3' -> Quadruple,
    '7' -> Quadruple,
    'b' -> Quadruple,
    'f' -> Quadruple,
  )

  private val notesMap: Map[Char, Tonic => Direction => Height] = Map(
    '0' -> (n => n.octave),
    '1' -> (n => n.`2m`),
    '2' -> (n => n.`2M`),
    '3' -> (n => n.`3m`),
    '4' -> (n => n.`3M`),
    '5' -> (n => n.`4`),
    '6' -> (n => n.`5dim`),
    '7' -> (n => n.`5j`),
    '8' -> (n => n.`6m`),
    '9' -> (n => n.`6M`),
    'a' -> (n => n.`7m`),
    'b' -> (n => n.`7M`),
    'c' -> (n => n.octave),
    'd' -> (n => n.octave),
    'e' -> (n => n.octave),
    'f' -> (n => n.octave),
  )
}
