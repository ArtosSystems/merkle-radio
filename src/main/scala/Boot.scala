import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.HttpMethods.{CONNECT, GET}
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives.{complete, handleWebSocketMessages, onComplete, path}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import io.aventus.service.akka.auth.AppAuthentication.AllowAllAuthentication
import io.aventus.service.akka.services.APIService
import io.aventus.service.akka.utils.AllowAllSchemeFilter
import io.aventus.service.akka.{AllowCORS, ServiceDefinition}
import music._
import websocket.actors.ClientHandlerActor
import websocket.actors.ClientHandlerActor.GetWebsocketFlow

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Boot extends {
  implicit val config: AppConfig = AppConfig()
  implicit val system: ActorSystem = ActorSystem(config.actorSystemName, ConfigFactory.load())
  implicit val mat: ActorMaterializer = ActorMaterializer()
} with APIService[Unit, AppConfig]()
  with AllowCORS
  with AllowAllAuthentication
  with AllowAllSchemeFilter
  with StrictLogging
  with App {

  def tonic = Tonic(440)

  def beatMaker = BeatMaker()

  override protected def initServices(implicit ec: ExecutionContext): Future[ServiceDefinition[Unit]] = Future {
    val handler = system.actorOf(ClientHandlerActor.props(beatMaker))

    def routes: Route = {
      path("ws") {
        val futureFlow =
          (handler ? GetWebsocketFlow) (3.seconds).mapTo[Flow[Message, Message, _]]

        onComplete(futureFlow) {
          case Success(flow) => handleWebSocketMessages(flow)
          case Failure(err) => complete(err.toString)
        }
      }
    }

    val serviceDefinition = ServiceDefinition(
      (_: Unit) => routes,
      Set()
    )
    serviceDefinition
  }

  override val allowedMethods = scala.collection.immutable.Seq(GET, CONNECT)
}
