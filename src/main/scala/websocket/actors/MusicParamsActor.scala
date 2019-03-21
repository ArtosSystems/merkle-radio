package websocket.actors

import akka.actor.{Actor, Props}
import websocket.actors.MusicParamsActor._

class MusicParamsActor extends Actor {
  override def receive: Receive = onMessage(100, 440)

  private def onMessage(currentBpm: Int, currentTonic: Int): Receive = {

    case ChangeBpm(beats) =>
      println(s"$currentBpm -> $beats")
      context.become(onMessage(beats, currentTonic))

    case ChangeTonic(tonic) =>
      println(s"$currentTonic -> $tonic")
      context.become(onMessage(currentBpm, tonic))

    case GetBpm =>
      sender() ! BpmValue(currentBpm)

    case GetTonic =>
      sender() ! TonicValue(currentTonic)
  }
}

object MusicParamsActor{
  case class ChangeBpm(bpm: Int)
  case class ChangeTonic(tonic: Int)

  case class BpmValue(value: Int)
  case class TonicValue(value: Int)

  case object GetBpm
  case object GetTonic

  def props = Props(new MusicParamsActor)
}

