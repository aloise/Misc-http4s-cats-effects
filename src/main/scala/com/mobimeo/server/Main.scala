package com.mobimeo.server

import cats.effect.IO
import com.mobimeo.datasource.csv.CsvTransportationDatasource
import com.mobimeo.server.http.DefaultHttpService
import com.mobimeo.service.AsyncTransportationTimeTableService
import fs2.StreamApp

import scala.util.Try

object Main extends StreamApp[IO] {

  import scala.concurrent.ExecutionContext.Implicits.global

  private def loadConfig:IO[ServerConfiguration] =
    IO.fromEither(Try(pureconfig.loadConfigOrThrow[ServerConfiguration]("server")).toEither)

  /**
    * Composing server instance
    * @return
    */
  private def httpServer = {
    for {
      config <-loadConfig
      csvDatasource = CsvTransportationDatasource.fromResources[IO]()
      timeTableService = AsyncTransportationTimeTableService(csvDatasource)
      server = DefaultHttpService(config)(timeTableService)
    } yield server.stream
  }

  /**
    * Running the app
    * @param args
    * @param requestShutdown
    * @return
    */
  def stream(args: List[String], requestShutdown: IO[Unit]) =
    httpServer.unsafeRunSync()



}
