import music.{BeatMaker, Note, Up}

object Boot extends App {
  val beatMaker = BeatMaker()

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
}
