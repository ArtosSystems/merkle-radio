package websocket.actors

import akka.actor.{Actor, Props}
import websocket.actors.BpmActor._

class BpmActor extends Actor {
  println("creation! " + context.self.path)

  private val currentBpm = 100

  override def receive: Receive = onMessage(currentBpm)

  private def onMessage(currentBpm: Int): Receive = {

    case ChangeBpm(beats) =>
      println(s"$currentBpm -> $beats")
      context.become(onMessage(beats))

    case GetBpm =>
      println(currentBpm)
      sender() ! Bpm(currentBpm)
  }
}

object BpmActor{
  case class ChangeBpm(bpm: Int)
  case class Bpm(value: Int)
  case object GetBpm

  def props = Props(new BpmActor)
}

