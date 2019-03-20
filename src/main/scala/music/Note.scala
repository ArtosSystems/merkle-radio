package music

import io.circe.Encoder
import io.circe.generic.semiauto._

sealed trait Note {
  val rhythm: Rhythm

  def duration(tempo: Int): Double = {
    val d = 60 / tempo.toDouble

    rhythm match {
      case White => d * 2
      case Black => d
      case DDouble => d / 2
      case Quadruple => d / 4
      case InterNote => d / 8
    }
  }
}
case class Rest(rhythm: Rhythm) extends Note
case object QuickStop extends Note {
  override val rhythm: Rhythm = InterNote
}
case class Sound(frequency: Double, rhythm: Rhythm) extends Note {
  lazy val toTonic: Tonic = Tonic(frequency)
}

object Note {
  implicit val noteEncoder: Encoder[Note] = deriveEncoder
  implicit val restEncoder: Encoder[Rest] = deriveEncoder
}

sealed trait Direction
case object Up extends Direction
case object Down extends Direction

sealed trait Rhythm
case object White extends Rhythm
case object Black extends Rhythm
case object DDouble extends Rhythm
case object Quadruple extends Rhythm
case object InterNote extends Rhythm

object Rhythm {
  implicit val encoder: Encoder[Rhythm] = Encoder[String].contramap({
    case _: White.type => "white"
    case _: Black.type => "black"
    case _: DDouble.type => "double"
    case _: Quadruple.type => "quadruple"
  })
}