package websocket.actors

import akka.actor.{Actor, Props}
import websocket.actors.MasterActor.PingServer

class KeepAliveActor extends Actor{

  override def receive: Receive = {
    case PingServer => {
      println("Pinged it!!")
      // TODO add web socket client...
    }
  }
}


object KeepAliveActor{
  def props = Props(new KeepAliveActor())
}
