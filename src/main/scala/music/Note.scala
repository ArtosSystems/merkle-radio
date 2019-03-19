package music

import io.circe.Encoder
import io.circe.generic.semiauto._

case class Note(frequency: Double, rhythm: Rhythm) {
  private lazy val intervalGap = Math.pow(2.0, 1.0/12)

  def duration(tempo: Int): Double = {
    val d = 60 / tempo.toDouble

    rhythm match {
      case White => d * 2
      case Black => d
      case DDouble => d / 2
      case Quadruple => d / 4
    }
  }

  def `2m` = gap(1)(_, _)
  def `2M` = gap(2)(_, _)
  def `3m` = gap(3)(_, _)
  def `3M` = gap(4)(_, _)
  def `4` = gap(5)(_, _)
  def `5dim` = gap(6)(_, _)
  def `5j` = gap(7)(_, _)
  def `6m` = gap(8)(_, _)
  def `6M` = gap(9)(_, _)
  def `7m` = gap(10)(_, _)
  def `7M` = gap(11)(_, _)
  def octave = gap(12)(_, _)

  private def gap(interval: Int)(direction: Direction, rhythm: Rhythm) = direction match {
    case Up   => copy(
      rhythm = rhythm,
      frequency = frequency * Math.pow(intervalGap, interval.toDouble)
    )

    case Down => copy(
      rhythm = rhythm,
      frequency = frequency * Math.pow(intervalGap, -interval.toDouble)
    )
  }
}

object Note {
  implicit val encoder: Encoder[Note] = deriveEncoder
}

sealed trait Direction
case object Up extends Direction
case object Down extends Direction

sealed trait Rhythm
case object White extends Rhythm
case object Black extends Rhythm
case object DDouble extends Rhythm
case object Quadruple extends Rhythm

object Rhythm {
  implicit val encoder: Encoder[Rhythm] = Encoder[String].contramap({
    case _: White.type => "white"
    case _: Black.type => "black"
    case _: DDouble.type => "double"
    case _: Quadruple.type => "quadruple"
  })
}