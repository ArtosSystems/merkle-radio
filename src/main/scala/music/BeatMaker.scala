package music

import com.jsyn.{JSyn, Synthesizer}
import com.jsyn.unitgen.{LineOut, SawtoothOscillatorBL}

class BeatMaker(synthesizer: Synthesizer, osc: SawtoothOscillatorBL, lineOut: LineOut) {
  def play(tempo: Int)(note: Note): Unit = note match {
    case _: Rest =>
      synthesizer.sleepFor(note.duration(tempo))

    case Sound(frequency, _) =>
      osc.start()
      lineOut.start()

      osc.frequency.set(frequency)
      osc.amplitude.set(0.8)

      synthesizer.sleepFor(note.duration(tempo))
      osc.stop()
      lineOut.stop()
  }

  def stop(): Unit = {
    osc.stop()
    lineOut.stop()
    synthesizer.stop()
  }
}

object BeatMaker {
  def apply(): BeatMaker = {
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
    lineOut.start()
    synthesizer.start()

    new BeatMaker(synthesizer, osc, lineOut)
  }
}