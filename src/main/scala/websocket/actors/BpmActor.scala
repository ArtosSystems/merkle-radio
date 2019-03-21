package websocket.actors

import akka.actor.{Actor, Props}
import websocket.actors.BpmActor._
import websocket.actors.MasterActor.GetBpm


class BpmActor extends Actor {

  private var currentBpm = 100

  override def receive: Receive = {

    case ChangeBpm(beats)    => currentBpm = beats

    case GetBpm              => sender() ! Bpm(currentBpm)
  }
}

object BpmActor{
  case class ChangeBpm(bpm: Int)
  case class Bpm(value: Int)

  def props = Props(new BpmActor)
}

