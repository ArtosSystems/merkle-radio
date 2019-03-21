package websocket.actors

import akka.NotUsed
import akka.actor.{Actor, ActorContext, ActorRef, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import music.{BeatMaker, Note}
import websocket.actors.BpmActor.ChangeBpm
import websocket.actors.MasterActor._

class MasterActor(beatMaker: BeatMaker, notes: Source[Note, NotUsed])(implicit mat: Materializer) extends Actor {

  private val keepAliveActor = createKeepAliveActor

  private val bpmActor = createBpmActor()

  override def receive: Receive = {

    case PingServer         => keepAliveActor ! PingServer

    case GetBpm             => bpmActor forward  GetBpm

    case msg @ ChangeBpm(_) => bpmActor ! msg
  }
}


object MasterActor {
  case object Start
  case object PingServer
  case object GetBpm
  case class Notes(source: Source[Note, NotUsed])
  def props(beatMaker: BeatMaker, activities: Source[Note, NotUsed])(implicit mat: Materializer) = Props(new MasterActor(beatMaker, activities))
  def createBpmActor()(implicit context: ActorContext): ActorRef = context.actorOf(BpmActor.props)
  def createKeepAliveActor(implicit context: ActorContext): ActorRef = context.actorOf(KeepAliveActor.props)
}
