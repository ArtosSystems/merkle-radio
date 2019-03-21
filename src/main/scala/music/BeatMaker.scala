package music

import com.jsyn.{JSyn, Synthesizer}
import com.jsyn.unitgen.{LineOut, SawtoothOscillatorBL}

class BeatMaker(synthesizer: Synthesizer, osc1: SawtoothOscillatorBL, osc2: SawtoothOscillatorBL, osc3: SawtoothOscillatorBL, lineOut: LineOut) {
  def play(tempo: Int)(note: Note): String = {
    val duration = note.duration(tempo)

    note match {
      case Sound(frequency, _, _) =>
        osc1.start()
        osc2.start()
        osc3.start()
        lineOut.start()

        osc1.frequency.set(frequency)
        osc1.amplitude.set(0.8)

        osc2.frequency.set(frequency * 2)
        osc2.amplitude.set(0.6)

        osc3.frequency.set(frequency * 3/2)
        osc3.amplitude.set(0.5)

        synthesizer.sleepFor(duration)

        osc1.stop()
        osc2.stop()
        osc3.stop()
        lineOut.stop()
        s"""{"frequency": $frequency, "duration": $duration}"""

      case _ =>
        synthesizer.sleepFor(note.duration(tempo))
        s"""{"duration": $duration}"""
    }
  }

  def stop(): Unit = {
    osc1.stop()
    osc2.stop()
    lineOut.stop()
    synthesizer.stop()
  }
}

object BeatMaker {
  def apply(): BeatMaker = {
    // Start JSyn synthesizer.
    val synthesizer: Synthesizer = JSyn.createSynthesizer()

    // Create some unit generators.
    val osc1 = new SawtoothOscillatorBL
    val osc2 = new SawtoothOscillatorBL
    val osc3 = new SawtoothOscillatorBL
    val lineOut = new LineOut

    synthesizer.add(osc1)
    synthesizer.add(osc2)
    synthesizer.add(osc3)
    synthesizer.add(lineOut)

    // Connect oscillator to both left and right channels of output.
    osc1.output.connect(0, lineOut.input, 0)
    osc1.output.connect(0, lineOut.input, 1)
    osc2.output.connect(0, lineOut.input, 0)
    osc2.output.connect(0, lineOut.input, 1)
    osc3.output.connect(0, lineOut.input, 0)
    osc3.output.connect(0, lineOut.input, 1)

    // Start the unit generators so they make sound.
    lineOut.start()
    synthesizer.start()

    new BeatMaker(synthesizer, osc1, osc2, osc3, lineOut)
  }
}