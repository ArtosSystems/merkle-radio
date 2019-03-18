name := "merkle-radio"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.21",
  "com.jsyn" % "jsyn" % "20170815",
)

resolvers ++= Seq[Resolver](
  "Clojars" at "https://clojars.org/repo"
)