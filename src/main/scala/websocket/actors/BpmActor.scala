package websocket.actors

import akka.actor.{Actor, Props}
import websocket.actors.BpmActor._
import websocket.actors.MasterActor.GetBpm


class BpmActor extends Actor {
  println("creation bpm actor! " + context.self.path)

  override def receive: Receive = onMessage(currentBpm = 10)

  private def onMessage(currentBpm: Int): Receive = {

    case ChangeBpm(beats) => context.become(onMessage(beats))

    case GetBpm => sender() ! Bpm(currentBpm)
  }
}

object BpmActor{
  case class ChangeBpm(bpm: Int)
  case class Bpm(value: Int)

  def props = Props(new BpmActor)
}

