package websocket.actors

import akka.NotUsed
import akka.actor.{Actor, ActorContext, ActorRef, Props}
import akka.stream.scaladsl.Source
import music.Note
import websocket.actors.BpmActor.{Bpm, ChangeBpm}
import websocket.actors.MasterActor._

class MasterActor extends Actor {

  private val keepAliveActor = createKeepAliveActor

  private val bpmActor = createBpmActor()

  override def receive: Receive = {

    case PingServer         => keepAliveActor ! PingServer

    case GetBpm             => bpmActor forward  GetBpm

    case msg @ ChangeBpm(_) => bpmActor ! msg
  }
}


object MasterActor {
  case object PingServer
  case object GetBpm
  case class Notes(source: Source[Note, NotUsed])
  def props = Props(new MasterActor())
  def createBpmActor()(implicit context: ActorContext): ActorRef = context.actorOf(BpmActor.props)
  def createKeepAliveActor(implicit context: ActorContext): ActorRef = context.actorOf(KeepAliveActor.props)
}
