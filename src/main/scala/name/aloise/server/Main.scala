package name.aloise.server

import cats.effect.IO
import name.aloise.datasource.csv.CsvTransportationDatasource
import name.aloise.server.http.DefaultHttpService
import name.aloise.service.AsyncTransportationTimeTableService
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
