package websocket.actors

import akka.actor.{Actor, Props}
import websocket.actors.BpmActor._
import websocket.actors.MasterActor.GetBpm


class BpmActor extends Actor {
  println("creation bpm actor! " + context.self.path)

  private var currentBpm = 10

  override def receive: Receive = {

    case ChangeBpm(beats)    =>
      //println(s"$currentBpm -> $beats")
      currentBpm = beats

    case GetBpm              =>
      //println(currentBpm)
      sender() ! Bpm(currentBpm)
  }
}

object BpmActor{
  case class ChangeBpm(bpm: Int)
  case class Bpm(value: Int)

  def props = Props(new BpmActor)
}

