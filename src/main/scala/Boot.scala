import BeatMaker.{Note, Up}
import com.jsyn.{JSyn, Synthesizer}
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.SawtoothOscillatorBL

object Boot extends App {
  val beatMaker = init()

  val tonic = Note(440, .5)

  beatMaker.play(tonic)
  beatMaker.play(tonic.`2M`(Up))
  beatMaker.play(tonic.`3M`(Up))
  beatMaker.play(tonic.`4`(Up))
  beatMaker.play(tonic.`5j`(Up))
  beatMaker.play(tonic.`6M`(Up))
  beatMaker.play(tonic.`7M`(Up))
  beatMaker.play(tonic.octave(Up))

  beatMaker.stop()

  private def init() = {
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

    new BeatMaker(synthesizer, osc, lineOut: LineOut)
  }
}
