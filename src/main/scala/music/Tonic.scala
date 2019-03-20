package music

case class Tonic(frequency: Double) {
  private lazy val intervalGap = Math.pow(2.0, 1.0/12)

  def `2m`: (Direction, Rhythm) => Note = gap(1)
  def `2M`: (Direction, Rhythm) => Note = gap(2)
  def `3m`: (Direction, Rhythm) => Note = gap(3)
  def `3M`: (Direction, Rhythm) => Note = gap(4)
  def `4`: (Direction, Rhythm) => Note = gap(5)
  def `5dim`: (Direction, Rhythm) => Note = gap(6)
  def `5j`: (Direction, Rhythm) => Note = gap(7)
  def `6m`: (Direction, Rhythm) => Note = gap(8)
  def `6M`: (Direction, Rhythm) => Note = gap(9)
  def `7m`: (Direction, Rhythm) => Note = gap(10)
  def `7M`: (Direction, Rhythm) => Note = gap(11)
  def octave: (Direction, Rhythm) => Note = gap(12)
  def rest: Rhythm => Note = Rest

  private def gap(interval: Int)(direction: Direction, rhythm: Rhythm) = direction match {
    case Up   => Sound(
      rhythm = rhythm,
      frequency = frequency * Math.pow(intervalGap, interval.toDouble)
    )

    case Down => Sound(
      rhythm = rhythm,
      frequency = frequency * Math.pow(intervalGap, -interval.toDouble)
    )
  }
}
