package websocket.actors

import akka.actor.Actor
import music.BeatMaker
import websocket.actors.BeethovenActor.{ChangeBPM, Play}

class BeethovenActor(beatMaker: BeatMaker) extends Actor{

  var bpm = 100

  override def receive: Receive = {
    case ChangeBPM(beats) => bpm = beats
    case Play             => beatMaker.play(bpm)
  }
}

object BeethovenActor{
  case class ChangeBPM(bpm: Int)
  case object Play
}
