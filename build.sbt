name := "challenge-mobimeo-v1"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions += "-Ypartial-unification"

val enumeratumVersion = "1.5.13"
val kantanCsvVersion = "0.4.0"
val catsVersion = "1.2.0"

libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.3.5",
  "com.github.pureconfig" %% "pureconfig" % "0.9.1",
  "com.beachape" %% "enumeratum" % enumeratumVersion,
  "com.nrinaudo" %% "kantan.csv" % kantanCsvVersion,
  "com.nrinaudo" %% "kantan.csv-cats" % kantanCsvVersion,
  "com.nrinaudo" %% "kantan.csv-generic" % kantanCsvVersion,
  "com.nrinaudo" %% "kantan.csv-enumeratum" % kantanCsvVersion,
  "com.nrinaudo" %% "kantan.csv-java8" % kantanCsvVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % "1.0.0-RC2",

  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
)