package decoder

import akka.NotUsed
import akka.stream.scaladsl.Flow
import music.{Note, Rhythm}

trait MerkleRootDecoder {
  def decode: Flow[String, Rhythm => Note, NotUsed]
}
