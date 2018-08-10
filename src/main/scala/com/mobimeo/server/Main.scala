package com.mobimeo.server

import cats.effect.IO
import com.mobimeo.datasource.csv.CsvTransportationDatasource
import com.mobimeo.server.http.DefaultHttpService
import com.mobimeo.service.AsyncTransportationTimeTableService
import fs2.StreamApp

import scala.util.Try

object Main extends StreamApp[IO] with DefaultServerBuilder {

  // just a quick default - might make sense to provide a separate EC
  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * Starting the app
    */
  def stream(args: List[String], requestShutdown: IO[Unit]) =
    httpServer().unsafeRunSync.stream



}
