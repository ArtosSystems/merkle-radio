package decoder

import akka.NotUsed
import akka.stream.scaladsl.Flow
import io.artos.activities.MerkleTreeCreatedActivity
import music.Note

trait MerkleRootDecoder {
  def decode: Flow[MerkleTreeCreatedActivity, Note, NotUsed]
}
