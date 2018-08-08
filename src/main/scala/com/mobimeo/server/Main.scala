package com.mobimeo.server

import cats.effect.IO
import com.mobimeo.datasource.TransportationDatasource
import com.mobimeo.datasource.csv.CsvTransportationDatasource
import com.mobimeo.service.ServerConfiguration

import scala.io.Source

object Main extends App {

  lazy val serverConfig = pureconfig.loadConfigOrThrow[ServerConfiguration]("server")
  val csvDatasource: TransportationDatasource[IO] = CsvTransportationDatasource(
    ioFromCSVDataResource("delays"),
    ioFromCSVDataResource("lines"),
    ioFromCSVDataResource("stops"),
    ioFromCSVDataResource("times")
  )

  def ioFromCSVDataResource(filename: String): IO[String] =
    IO.pure(Source.fromResource("data/" + filename + ".csv").mkString)


  println("Hello",
    serverConfig,
    csvDatasource.getDelays.unsafeRunSync(),
    // csvDatasource.getLines.unsafeRunSync(),
    //csvDatasource.getStops.unsafeRunSync(),
    // csvDatasource.getTimes.unsafeRunSync()
  )

}
