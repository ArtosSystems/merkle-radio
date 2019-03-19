object Versions {
    val baseVersion = "0.0.1"

    private val branch = sys.env.get("BRANCH_NAME").map(_.replaceAll("""\W""", "_"))
    private lazy val snapshotVersion = s"$baseVersion-${branch.getOrElse("")}-${sys.env.getOrElse("BUILD_NUMBER", "DEV")}"
    private lazy val snapshotVersions = (s"SNAPSHOT-$snapshotVersion", Seq(s"SNAPSHOT-$snapshotVersion"), s"$snapshotVersion-SNAPSHOT")

    val (containerVersion, containerTags, libraryVersion) = branch.fold {
      println("$BRANCH_NAME environment variable not defined, creating snapshot build")
      snapshotVersions
    }{
      branchName =>
        val develop = branch.contains("develop")
        println(s"$$BRANCH_NAME environment set to $branchName, creating ${if (develop) "release" else "snapshot"} build")

        if (develop) {
          (baseVersion, Seq(baseVersion, "latest"), baseVersion)
        } else {
          snapshotVersions
        }
    }
}
