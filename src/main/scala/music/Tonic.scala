package music

case class Tonic(frequency: Double, totalGaps: Int = 0) {
  private lazy val intervalGap = Math.pow(2.0, 1.0/12)

  def tonic: (Direction, Rhythm) => Sound = gap(0)
  def `2m`: (Direction, Rhythm) => Sound = gap(1)
  def `2M`: (Direction, Rhythm) => Sound = gap(2)
  def `3m`: (Direction, Rhythm) => Sound = gap(3)
  def `3M`: (Direction, Rhythm) => Sound = gap(4)
  def `4`: (Direction, Rhythm) => Sound = gap(5)
  def `5dim`: (Direction, Rhythm) => Sound = gap(6)
  def `5j`: (Direction, Rhythm) => Sound = gap(7)
  def `6m`: (Direction, Rhythm) => Sound = gap(8)
  def `6M`: (Direction, Rhythm) => Sound = gap(9)
  def `7m`: (Direction, Rhythm) => Sound = gap(10)
  def `7M`: (Direction, Rhythm) => Sound = gap(11)
  def octave: (Direction, Rhythm) => Sound = gap(12)
  def rest: Rhythm => Note = Rest

  private def gap(interval: Int)(direction: Direction, rhythm: Rhythm) = direction match {
    case Up   => Sound(
      rhythm = rhythm,
      frequency = frequency * Math.pow(intervalGap, interval.toDouble),
      intervalGap = interval + totalGaps
    )

    case Down => Sound(
      rhythm = rhythm,
      frequency = frequency * Math.pow(intervalGap, -interval.toDouble),
      intervalGap = -interval + totalGaps
    )
  }
}
