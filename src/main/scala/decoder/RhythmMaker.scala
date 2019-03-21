package decoder

import akka.NotUsed
import akka.stream.scaladsl.Flow
import music._

object RhythmMaker {
  def produceRhythm: Flow[Char, Rhythm, NotUsed] = Flow[Char]
    .mapConcat { c =>
      val pattern = rhythmMap(c)
      pattern ++ pattern
    }

  private val rhythmMap: Map[Char, List[Rhythm]] = Map(
    '0' -> List(White, White, Black, Black, Black, Black),
    '1' -> List(Black, Black, Black, Black, White, White),
    '2' -> List(Black, Black, White, Black, Black, White),
    '3' -> List(White, Black, Black, White, Black, Black),
    '4' -> List(Black, White, Black, Black, White, Black),
    '5' -> List(Black, DDouble, DDouble, Black, DDouble, DDouble),
    '6' -> List(DDouble, DDouble, Black, DDouble, DDouble, Black),
    '7' -> List(DDouble, Black, DDouble, Black, DDouble, DDouble),
    '8' -> List(Black, DDouble, DDouble, DDouble, DDouble, Black),
    '9' -> List(DDouble, DDouble, DDouble, DDouble, Black, White),
    'a' -> List(White, Black, Quadruple, Quadruple, Quadruple, Quadruple),
    'b' -> List(Quadruple, Quadruple, White, Quadruple, Quadruple, Black),
    'c' -> List(Black, DDouble, DDouble, DDouble, DDouble, Black),
    'd' -> List(DDouble, DDouble, DDouble, Quadruple, Quadruple, White),
    'e' -> List(Quadruple, Quadruple, Quadruple, Quadruple, Black, White),
    'f' -> List(Quadruple, Quadruple, Quadruple, Quadruple, White, Black),
  )
}
