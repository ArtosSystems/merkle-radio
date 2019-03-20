package websocket

import akka.NotUsed
import akka.http.scaladsl.model.ws._
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import io.circe.syntax._
import music.Note

import scala.language.higherKinds

class ServiceHandlers(noteSource: Source[Note, NotUsed])(implicit mat: Materializer) {

  val noteService: Flow[Message, TextMessage, NotUsed] =
    Flow[Message]
      .mapConcat {
        case _ : TextMessage    => TextMessage(noteSource.map(_.asJson.noSpaces)) :: Nil
        case bm: BinaryMessage  =>
          // ignore binary messages but drain content to avoid the stream being clogged
          bm.dataStream.runWith(Sink.ignore)
          Nil
      }

  val bpmService: Flow[Message, TextMessage, NotUsed] =
    Flow[Message]
      .mapConcat {
        case _ : TextMessage    => TextMessage(noteSource.map(_.asJson.noSpaces)) :: Nil
        case bm: BinaryMessage  =>
          // ignore binary messages but drain content to avoid the stream being clogged
          bm.dataStream.runWith(Sink.ignore)
          Nil
      }
}
