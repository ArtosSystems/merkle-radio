package websocket

import akka.NotUsed
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow

import scala.language.higherKinds

class WsServer private(handlers: ServiceHandlers)(implicit mat: Materializer) {

  val requestHandler: HttpRequest => HttpResponse = {

    case req @ HttpRequest(GET, Uri.Path("/note-stream"), _, _, _)  => handleWsRequest(req, handlers.noteService)

    case req @ HttpRequest(GET, Uri.Path("/change-bpm"), _, _, _)   => handleWsRequest(req, handlers.bpmService)

    case r: HttpRequest =>
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      HttpResponse(404, entity = "Unknown resource!")
  }

  private def handleWsRequest(req: HttpRequest, handler: Flow[Message, TextMessage, NotUsed]) = {
    req.header[UpgradeToWebSocket] match {
      case Some(upgrade) => upgrade.handleMessages(handler)
      case None          => HttpResponse(400, entity = "Not a valid websocket request!")
    }
  }
}

object WsServer{
  def apply(handlers: ServiceHandlers)(implicit mat: Materializer) = new WsServer(handlers)
}
