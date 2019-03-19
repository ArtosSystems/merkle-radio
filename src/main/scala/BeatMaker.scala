import BeatMaker.Note
import com.jsyn.Synthesizer
import com.jsyn.unitgen.{LineOut, SawtoothOscillatorBL}

class BeatMaker(synthesizer: Synthesizer, osc: SawtoothOscillatorBL, lineOut: LineOut) {
  def play(note: Note): Unit = {
    println(note.frequency)
    osc.frequency.set(note.frequency)
    osc.amplitude.set(0.8)

    synthesizer.sleepFor(note.duration)
  }

  def stop(): Unit = {
    osc.stop()
    lineOut.stop()
    synthesizer.stop()
  }
}

object BeatMaker {
  private val intervalGap = Math.pow(2.0, 1.0/12)

  case class Note(frequency: Double, duration: Double) {
    def `2m`(direction: Direction) = gap(1)
    def `2M`(direction: Direction) = gap(2)
    def `3m`(direction: Direction) = gap(3)
    def `3M`(direction: Direction) = gap(4)
    def `4`(direction: Direction) = gap(5)
    def `5dim`(direction: Direction) = gap(6)
    def `5j`(direction: Direction) = gap(7)
    def `6m`(direction: Direction) = gap(8)
    def `6M`(direction: Direction) = gap(9)
    def `7m`(direction: Direction) = gap(10)
    def `7M`(direction: Direction) = gap(11)
    def octave(direction: Direction) = copy(frequency = frequency * 2)

    private def gap(interval: Int) = copy(frequency = frequency * Math.pow(intervalGap, interval))
  }

  sealed trait Direction
  case object Up extends Direction
  case object Down extends Direction
}
