scalacOptions += "-Ypartial-unification"

val http4sVersion = "0.18.14"
val specs2Version = "4.2.0"
val logbackVersion = "1.2.3"
val circeVersion = "0.9.3"
val enumeratumVersion = "1.5.13"
val kantanCsvVersion = "0.4.0"
val catsVersion = "1.2.0"
val scalaCacheVersion = "0.24.2"

dockerExposedPorts := Seq(8081)

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    Defaults.itSettings,
    organization := "name.aloise",
    name := "mobile-transportation-test-service-v1",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-java8" % circeVersion,
      "com.github.tototoshi" %% "scala-csv" % "1.3.5",
      "com.github.pureconfig" %% "pureconfig" % "0.9.1",
      "com.beachape" %% "enumeratum" % enumeratumVersion,
      "com.nrinaudo" %% "kantan.csv" % kantanCsvVersion,
      "com.nrinaudo" %% "kantan.csv-cats" % kantanCsvVersion,
      "com.nrinaudo" %% "kantan.csv-generic" % kantanCsvVersion,
      "com.nrinaudo" %% "kantan.csv-enumeratum" % kantanCsvVersion,
      "com.nrinaudo" %% "kantan.csv-java8" % kantanCsvVersion,
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % "0.10.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
      "com.github.cb372" %% "scalacache-core" % scalaCacheVersion,
      "com.github.cb372" %% "scalacache-cats-effect" % scalaCacheVersion,
      "com.github.cb372" %% "scalacache-caffeine" % scalaCacheVersion,

      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "com.github.agourlay" %% "cornichon-scalatest" % "0.16.1" % IntegrationTest

    )
  )
