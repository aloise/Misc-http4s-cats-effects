package com.mobimeo.server

import cats.effect.IO
import com.mobimeo.datasource.csv.CsvTransportationDatasource
import com.mobimeo.server.http.DefaultHttpService
import com.mobimeo.service.AsyncTransportationTimeTableService

import scala.concurrent.ExecutionContext
import scala.util.Try

trait DefaultServerBuilder {

  private def loadConfig:IO[ServerConfiguration] =
    IO.fromEither(Try(pureconfig.loadConfigOrThrow[ServerConfiguration]("server")).toEither)

  /**
    * Composing server instance
    * @return
    */
  protected def httpServer(getConfig:IO[ServerConfiguration] = loadConfig)(implicit ec:ExecutionContext) = {
    for {
      config <- getConfig
      csvDatasource = CsvTransportationDatasource.fromResources[IO]()
      timeTableService = AsyncTransportationTimeTableService(csvDatasource)
      server = DefaultHttpService(config)(timeTableService)
    } yield server
  }

}
