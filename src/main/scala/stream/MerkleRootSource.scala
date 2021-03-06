package stream

import akka.stream.scaladsl.Source
import io.artos.activities.{MerkleTreeCreatedActivity, TraceData}

import scala.util.Random

class MerkleRootSource {
  val source = Source(Random.shuffle(List(
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x4e3b8f1522fb72a9394f55b6eca9e170f5123ea9d33bd71a531e6349dcd39948", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xf70aa4ea63a949796a604bba0f7ceb0031c320afba8cb1f3d4741657db2299e7", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x66aab022faea79d2bb35a1676d274ebb075457d0852db0b801cb85b83a16507a", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb2816cda9339f3f573f22dcc941d1ac84e402509d5edfd6d5cef79d42dafdddf", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb8699eab303b11e263c6b5b8a5e9cd60549eeeabcc21dd8e3e56172b24a20185", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x259e325e202e000f2d25fdda3760b159b4b7214d8717dd3deb6d382164602de", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x479c7558432b024abbc66c812dc1cbbd82494c56583efcca2464e7e594ef2cf", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe0a3bf93604bc08a013e74fc924b07f1f0e756aef2d699e59b33806ab689de53", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xf13aaaf1b9607d004623c872f59d67ee0d77bade9a226218a6b31a3f966fbe32", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xbb7c3f9208a9360b3fbbefa619c76c8f3000422fd4ae4f3c8e7d6030d4997e50", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x30df9bc0ae27a1445baa2678a5669f2ea305fb399bd415852009137d035e5f13", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x3b925ed11488ebb275ee7eaf3e98d425b14c70a7afcabf049127ca8503c17e3e", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8a12e057a4f02835fa8badedb5d9a163b52d70805b885d20abca6b64c20a60d", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xacd8d82a94a70aaf8c5d71c4020936847c17db95e1118103328b14b1a6164a1", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8e8b652a01996b462a760099fddb1045514594fc70ae6e109a8226ec27f911de", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xa173ad102f99eb220854ea6cc3a0190dcba835bf9b6f34eb73088e557acd27af", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x1b60b93253c960d4142d340bf5e6b907055e48d5e827cfbf8b358907dc8dbb2", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x323a9125df91f2e0b7dea70893fc500af600c499657e09332762cd4fa9745297", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xc3265c4f48b10d1dcaa81951a162a7c3ccd241f81494b1fe34ef977ea77302bd", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xead68295b4d1e290936fcc3b76961193c96360fd9be11fdb11f883e063d22b14", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x7cafc68c320e7e129b443af093fffc2f1bae327ab4307bdbf18a11da425b0b22", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x2534082818c97d91d3d526f176c83cb25362af41a115c51a399d72b73ede869f", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8020b22807efd264ea78ea513da909b8c4783b09f3d86fd519aebcd6ce752c6b", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x6ced81fd66ee406f2a3526c9df4549a46ce59a9efbc9efe8624602272594fbf5", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe11aaa41b8f072edea0526e3805747c7251915bc05cc646dd33b0076b0f5615c", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb2d967a3363a410c86ab464c249aefe560141e28308f96d537e6fe5827d61658", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xc8dd47085bb9380a554df3997e4304ed87b37bb57c7523d58a86278e6e38553a", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xf7c9adc29984ba83f2cfc5a34766744c7fb5c72c0137294560efdf97c36614b0", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe941f07b5d5e261fcd2a2391ba48985a2be64b9e6b96b8b09fd6480b3cd05a4a", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xbb7c3f9208a9360b3fbbefa619c76c8f3000422fd4ae4f3c8e7d6030d4997e50", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x3b925ed11488ebb275ee7eaf3e98d425b14c70a7afcabf049127ca8503c17e3e", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x30df9bc0ae27a1445baa2678a5669f2ea305fb399bd415852009137d035e5f13", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8a12e057a4f02835fa8badedb5d9a163b52d70805b885d20abca6b64c20a60d", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x259e325e202e000f2d25fdda3760b159b4b7214d8717dd3deb6d382164602de", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb2816cda9339f3f573f22dcc941d1ac84e402509d5edfd6d5cef79d42dafdddf", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x479c7558432b024abbc66c812dc1cbbd82494c56583efcca2464e7e594ef2cf", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb8699eab303b11e263c6b5b8a5e9cd60549eeeabcc21dd8e3e56172b24a20185", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8e8b652a01996b462a760099fddb1045514594fc70ae6e109a8226ec27f911de", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xa173ad102f99eb220854ea6cc3a0190dcba835bf9b6f34eb73088e557acd27af", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x1b60b93253c960d4142d340bf5e6b907055e48d5e827cfbf8b358907dc8dbb2", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xacd8d82a94a70aaf8c5d71c4020936847c17db95e1118103328b14b1a6164a1", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe0a3bf93604bc08a013e74fc924b07f1f0e756aef2d699e59b33806ab689de53", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xf13aaaf1b9607d004623c872f59d67ee0d77bade9a226218a6b31a3f966fbe32", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xc3265c4f48b10d1dcaa81951a162a7c3ccd241f81494b1fe34ef977ea77302bd", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x323a9125df91f2e0b7dea70893fc500af600c499657e09332762cd4fa9745297", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x7cafc68c320e7e129b443af093fffc2f1bae327ab4307bdbf18a11da425b0b22", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xead68295b4d1e290936fcc3b76961193c96360fd9be11fdb11f883e063d22b14", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8020b22807efd264ea78ea513da909b8c4783b09f3d86fd519aebcd6ce752c6b", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x6ced81fd66ee406f2a3526c9df4549a46ce59a9efbc9efe8624602272594fbf5", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe11aaa41b8f072edea0526e3805747c7251915bc05cc646dd33b0076b0f5615c", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb2d967a3363a410c86ab464c249aefe560141e28308f96d537e6fe5827d61658", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x2534082818c97d91d3d526f176c83cb25362af41a115c51a399d72b73ede869f", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xc8dd47085bb9380a554df3997e4304ed87b37bb57c7523d58a86278e6e38553a", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xf7c9adc29984ba83f2cfc5a34766744c7fb5c72c0137294560efdf97c36614b0", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe941f07b5d5e261fcd2a2391ba48985a2be64b9e6b96b8b09fd6480b3cd05a4a", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x30df9bc0ae27a1445baa2678a5669f2ea305fb399bd415852009137d035e5f13", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8a12e057a4f02835fa8badedb5d9a163b52d70805b885d20abca6b64c20a60d", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x3b925ed11488ebb275ee7eaf3e98d425b14c70a7afcabf049127ca8503c17e3e", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8e8b652a01996b462a760099fddb1045514594fc70ae6e109a8226ec27f911de", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xa173ad102f99eb220854ea6cc3a0190dcba835bf9b6f34eb73088e557acd27af", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x1b60b93253c960d4142d340bf5e6b907055e48d5e827cfbf8b358907dc8dbb2", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x479c7558432b024abbc66c812dc1cbbd82494c56583efcca2464e7e594ef2cf", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x259e325e202e000f2d25fdda3760b159b4b7214d8717dd3deb6d382164602de", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xf13aaaf1b9607d004623c872f59d67ee0d77bade9a226218a6b31a3f966fbe32", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb2816cda9339f3f573f22dcc941d1ac84e402509d5edfd6d5cef79d42dafdddf", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xacd8d82a94a70aaf8c5d71c4020936847c17db95e1118103328b14b1a6164a1", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xbb7c3f9208a9360b3fbbefa619c76c8f3000422fd4ae4f3c8e7d6030d4997e50", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb8699eab303b11e263c6b5b8a5e9cd60549eeeabcc21dd8e3e56172b24a20185", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe0a3bf93604bc08a013e74fc924b07f1f0e756aef2d699e59b33806ab689de53", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x323a9125df91f2e0b7dea70893fc500af600c499657e09332762cd4fa9745297", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xc3265c4f48b10d1dcaa81951a162a7c3ccd241f81494b1fe34ef977ea77302bd", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x7cafc68c320e7e129b443af093fffc2f1bae327ab4307bdbf18a11da425b0b22", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xead68295b4d1e290936fcc3b76961193c96360fd9be11fdb11f883e063d22b14", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8020b22807efd264ea78ea513da909b8c4783b09f3d86fd519aebcd6ce752c6b", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x6ced81fd66ee406f2a3526c9df4549a46ce59a9efbc9efe8624602272594fbf5", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe11aaa41b8f072edea0526e3805747c7251915bc05cc646dd33b0076b0f5615c", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb2d967a3363a410c86ab464c249aefe560141e28308f96d537e6fe5827d61658", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x2534082818c97d91d3d526f176c83cb25362af41a115c51a399d72b73ede869f", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe941f07b5d5e261fcd2a2391ba48985a2be64b9e6b96b8b09fd6480b3cd05a4a", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xc8dd47085bb9380a554df3997e4304ed87b37bb57c7523d58a86278e6e38553a", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xf7c9adc29984ba83f2cfc5a34766744c7fb5c72c0137294560efdf97c36614b0", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xbb7c3f9208a9360b3fbbefa619c76c8f3000422fd4ae4f3c8e7d6030d4997e50", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x1b60b93253c960d4142d340bf5e6b907055e48d5e827cfbf8b358907dc8dbb2", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xf13aaaf1b9607d004623c872f59d67ee0d77bade9a226218a6b31a3f966fbe32", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb2816cda9339f3f573f22dcc941d1ac84e402509d5edfd6d5cef79d42dafdddf", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x30df9bc0ae27a1445baa2678a5669f2ea305fb399bd415852009137d035e5f13", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xa173ad102f99eb220854ea6cc3a0190dcba835bf9b6f34eb73088e557acd27af", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xacd8d82a94a70aaf8c5d71c4020936847c17db95e1118103328b14b1a6164a1", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb8699eab303b11e263c6b5b8a5e9cd60549eeeabcc21dd8e3e56172b24a20185", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x259e325e202e000f2d25fdda3760b159b4b7214d8717dd3deb6d382164602de", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xe0a3bf93604bc08a013e74fc924b07f1f0e756aef2d699e59b33806ab689de53", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8a12e057a4f02835fa8badedb5d9a163b52d70805b885d20abca6b64c20a60d", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x3b925ed11488ebb275ee7eaf3e98d425b14c70a7afcabf049127ca8503c17e3e", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x479c7558432b024abbc66c812dc1cbbd82494c56583efcca2464e7e594ef2cf", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8e8b652a01996b462a760099fddb1045514594fc70ae6e109a8226ec27f911de", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xc3265c4f48b10d1dcaa81951a162a7c3ccd241f81494b1fe34ef977ea77302bd", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xead68295b4d1e290936fcc3b76961193c96360fd9be11fdb11f883e063d22b14", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x323a9125df91f2e0b7dea70893fc500af600c499657e09332762cd4fa9745297", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x7cafc68c320e7e129b443af093fffc2f1bae327ab4307bdbf18a11da425b0b22", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0xb2d967a3363a410c86ab464c249aefe560141e28308f96d537e6fe5827d61658", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x2534082818c97d91d3d526f176c83cb25362af41a115c51a399d72b73ede869f", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x8020b22807efd264ea78ea513da909b8c4783b09f3d86fd519aebcd6ce752c6b", 2, 0),
    MerkleTreeCreatedActivity(TraceData.newTrace("test"), "0x6ced81fd66ee406f2a3526c9df4549a46ce59a9efbc9efe8624602272594fbf5", 2, 0),
  )))
}
