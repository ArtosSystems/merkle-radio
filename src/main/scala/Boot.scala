import javax.sound.sampled.{AudioFormat, AudioSystem}

object Boot extends App {
  val duration = 1000
  var frequency = 440

  val intervalGap = Math.pow(2.0, 1/12)
  println(intervalGap) // TODO: this should round to 1.0

  play(440, 750)
//  play(440 * Math.pow(intervalGap, 2), 750)
//  play(440 * Math.pow(intervalGap, 4), 750)
//  play(440 * Math.pow(intervalGap, 5), 750)
//  play(440 * Math.pow(intervalGap, 7), 750)
//  play(440 * Math.pow(intervalGap, 9), 750)
//  play(440 * Math.pow(intervalGap, 11), 750)
  play(440 * Math.pow(intervalGap, 12), 750)

  def play(frequency: Double, duration: Int) = {
    val rate = 44100.toFloat
    val buf = new Array[Byte](1)
    val af = new AudioFormat(rate, 8, 1, true, false)
    val sdl = AudioSystem.getSourceDataLine(af)
    sdl.open()
    sdl.start()
    var i = 0
    while (i < duration * rate / 1000) {
      val angle = i / (rate / frequency) * 2.0 * Math.PI
      buf(0) = (Math.sin(angle) * 100).toByte
      sdl.write(buf, 0, 1)

      i += 1
      i - 1
    }
    sdl.drain()
  }
}
