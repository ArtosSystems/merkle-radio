import javax.sound.sampled.{AudioFormat, AudioSystem}

object Boot {

  def main(args: Array[String]): Unit = {
    val buf = new Array[Byte](1)
    val af = new AudioFormat(44100.toFloat, 8, 1, true, false)
    val sdl = AudioSystem.getSourceDataLine(af)
    sdl.open()
    sdl.start()
    var i = 0
    while (i < 1000 * 44100.toFloat / 1000) {
      val angle = i / (44100.toFloat / 440) * 2.0 * Math.PI
      buf(0) = (Math.sin(angle) * 100).toByte
      sdl.write(buf, 0, 1)

      {
        i += 1; i - 1
      }
    }
    sdl.drain()
  }
}
