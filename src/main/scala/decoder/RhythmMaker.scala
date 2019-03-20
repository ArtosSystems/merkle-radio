package decoder

import akka.NotUsed
import akka.stream.scaladsl.Flow
import music._

object RhythmMaker {
  def produceRhythm: Flow[Char, Rhythm, NotUsed] = Flow[Char]
    .mapConcat(rhythmMap)

  private val rhythmMap: Map[Char, List[Rhythm]] = Map(
    '0' -> List(White, White),
    '1' -> List(Black, Black, Black, Black),
    '2' -> List(Black, Black, White),
    '3' -> List(White, Black, Black),
    '4' -> List(Black, White, Black),
    '5' -> List(Black, DDouble, DDouble, Black, DDouble, DDouble),
    '6' -> List(DDouble, DDouble, Black, DDouble, DDouble, Black),
    '7' -> List(DDouble, Black, DDouble, Black, DDouble, DDouble),
    '8' -> List(Quadruple, Quadruple, Quadruple, Quadruple, DDouble, DDouble, DDouble, DDouble, Black),
    '9' -> List(DDouble, DDouble, DDouble, DDouble, Black, White),
    'a' -> List(White, Black, Quadruple, Quadruple, Quadruple, Quadruple),
    'b' -> List(Black, Quadruple, Quadruple, Black, Quadruple, Quadruple, Black),
    'c' -> List(Quadruple, DDouble, DDouble, Quadruple, Quadruple, DDouble, DDouble, Quadruple, DDouble, DDouble),
    'd' -> List(Black, DDouble, DDouble, DDouble, Quadruple, Quadruple, Black),
    'e' -> List(Quadruple, Quadruple, Quadruple, Quadruple, Black, White),
    'f' -> List(Quadruple, Quadruple, Quadruple, Quadruple, White, Black),
  )
}
