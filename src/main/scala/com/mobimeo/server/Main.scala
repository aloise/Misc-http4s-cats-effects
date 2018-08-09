package com.mobimeo.server

import cats.effect.{ExitCode, IO, IOApp}
import com.mobimeo.datasource.TransportationDatasource
import com.mobimeo.datasource.csv.CsvTransportationDatasource
import com.mobimeo.service.CachedTransportationTimeTableService

import scala.io.Source
import scala.util.Try

object Main extends IOApp {


  val csvDatasource: TransportationDatasource[IO] = CsvTransportationDatasource.fromResources()

  lazy val service = CachedTransportationTimeTableService(csvDatasource)


  def run(args: List[String]): IO[ExitCode] = {
    for {
      serverConfig <- IO.fromEither(Try(pureconfig.loadConfigOrThrow[ServerConfiguration]("server")).toEither)

      result <- IO.pure{
        println("Hello")
        ExitCode.Success
      }
    } yield result
  }

}
