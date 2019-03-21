package music

case class Tonic(frequency: Double, totalGaps: Int = 0) {
  private lazy val intervalGap = Math.pow(2.0, 1.0/12)

  def tonic: Direction => Height = gap(0)
  def `2m`: Direction => Height = gap(1)
  def `2M`: Direction => Height = gap(2)
  def `3m`: Direction => Height = gap(3)
  def `3M`: Direction => Height = gap(4)
  def `4`: Direction => Height = gap(5)
  def `5dim`: Direction => Height = gap(6)
  def `5j`: Direction => Height = gap(7)
  def `6m`: Direction => Height = gap(8)
  def `6M`: Direction => Height = gap(9)
  def `7m`: Direction => Height = gap(10)
  def `7M`: Direction => Height = gap(11)
  def octave: Direction => Height = gap(12)
//  def rest = Rest

  private def gap(interval: Int)(direction: Direction) = direction match {
    case Up   => Height(
      frequency = frequency * Math.pow(intervalGap, interval.toDouble),
      intervalGap = interval + totalGaps
    )

    case Down => Height(
      frequency = frequency * Math.pow(intervalGap, -interval.toDouble),
      intervalGap = -interval - totalGaps
    )
  }
}
