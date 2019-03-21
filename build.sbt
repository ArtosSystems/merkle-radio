import sbt.Keys._
import com.amazonaws.regions.{Region, Regions}
import Versions._
import sbtecr.EcrPlugin.autoImport.repositoryTags

inThisBuild(Seq(
  scalaOrganization := "org.typelevel",
  scalaVersion := "2.12.4-bin-typelevel-4"
))

val scalac = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ypartial-unification",
  "-Yliteral-types",
)

lazy val dockerSettings = Seq(
  dockerExposedPorts in Docker := Seq(8080),
  dockerBaseImage       := "openjdk:11-jre-slim",
  dockerRepository      := Some("763401534077.dkr.ecr.eu-west-2.amazonaws.com"),
  region            in Ecr := Region.getRegion(Regions.EU_WEST_2),
  repositoryName    in Ecr := (packageName in Docker).value,
  localDockerImage  in Ecr := (dockerRepository in Docker).value.get + "/" + (packageName in Docker).value + ":" + (version in Docker).value,
  login             in Ecr := ((login in Ecr) dependsOn (createRepository in Ecr)).value,
  push              in Ecr := ((push in Ecr) dependsOn (publishLocal in Docker, login in Ecr)).value,
  repositoryTags    in Ecr := containerTags
)

lazy val commonSettings = Seq(
  organization  := "io.artos",
  scalaVersion  := "2.12.4-bin-typelevel-4",
  name          := "merkle-radio",
  version       := libraryVersion,
  scalacOptions := scalac,
  resolvers ++= Seq[Resolver](
    "Clojars" at "https://clojars.org/repo",
    s3resolver.value("Aventus Releases resolver", s3("releases.repo.aventus.io")).withIvyPatterns,
    s3resolver.value("Aventus Snapshots resolver", s3("snapshots.repo.aventus.io")).withIvyPatterns,
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  ),
  s3region := Regions.EU_WEST_2,
  fork := true,
)

enablePlugins(JavaServerAppPackaging)
enablePlugins(AshScriptPlugin)
enablePlugins(DockerPlugin)
enablePlugins(EcrPlugin)

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings ++ dockerSettings,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka"             %% "akka-stream"                  % "2.5.21",
      "com.jsyn"                      %  "jsyn"                         % "20170815",
      "io.artos"                      %% "activity-model"               % "0.18.1",
      "io.aventus"                    %% "aventus4s"                    % "0.5.1-0.12",
      "io.aventus"                    %% "akka-service-utils"           % "1.16.3",
      "com.github.pureconfig"         %% "pureconfig"                   % "0.8.0",
      "com.typesafe.akka"             %% "akka-http"                    % "10.1.7"
    ),
    mainClass in assembly := Some("Boot"),
    assemblyJarName in assembly := "merkle-radio.jar",
    assemblyMergeStrategy in assembly := {
      case n if n.startsWith("reference.conf") => MergeStrategy.concat
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
  )

lazy val artefactVersion = taskKey[Unit]("Prints the version")
artefactVersion := println(Versions.baseVersion)
 