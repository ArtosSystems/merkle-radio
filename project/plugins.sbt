resolvers += Resolver.jcenterRepo
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.2")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.17.0")
addSbtPlugin("com.mintbeans" % "sbt-ecr" % "0.8.0")