import com.jsyn.{JSyn, Synthesizer}
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.SawtoothOscillatorBL

object Boot extends App {

  // Start JSyn synthesizer.
  val synthesizer: Synthesizer = JSyn.createSynthesizer()

  // Create some unit generators.
  val osc = new SawtoothOscillatorBL
  val lineOut = new LineOut

  synthesizer.add(osc)
  synthesizer.add(lineOut)

  // Connect oscillator to both left and right channels of output.
  osc.output.connect(0, lineOut.input, 0)
  osc.output.connect(0, lineOut.input, 1)

  // Start the unit generators so they make sound.
  osc.start()
  lineOut.start()
  synthesizer.start()

  val intervalGap = Math.pow(2.0, 1.0/12)

  play(Note(440, 1.0))
  play(Note(440 * Math.pow(intervalGap, 2), 1.0))
  play(Note(440 * Math.pow(intervalGap, 4), 1.0))
  play(Note(440 * Math.pow(intervalGap, 5), 1.0))
  play(Note(440 * Math.pow(intervalGap, 7), 1.0))
  play(Note(440 * Math.pow(intervalGap, 9), 1.0))
  play(Note(440 * Math.pow(intervalGap, 11), 1.0))
  play(Note(440 * Math.pow(intervalGap, 12), 1.0))

  // Stop units and delete them to reclaim their resources.
  osc.stop()
  lineOut.stop()
  synthesizer.stop()

  def play(note: Note): Unit = {
    osc.frequency.set(note.frequency)
    osc.amplitude.set(0.8)

    synthesizer.sleepFor(note.duration)
  }
}

case class Note(frequency: Double, duration: Double)