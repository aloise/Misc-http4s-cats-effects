package name.aloise.server

import cats.effect.IO
import fs2.StreamApp

import scala.util.{Failure, Success, Try}
import scala.concurrent.{Await, ExecutionContext, Future}
import scalaz.concurrent.{Task => ZTask}
import name.aloise.utils.FutureEffect._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object Main extends StreamApp[IO] with DefaultServerBuilder {

  /**
    * Starting the app
    */
// ScalaZ
// import io.chrisdavenport.scalaz.task._
//  def stream(args: List[String], requestShutdown: ZTask[Unit]) =
//    httpServer[ZTask]().unsafePerformSync.stream



// Cats IO
  override def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    httpServer[IO]().unsafeRunSync().stream


//  override def stream(args: List[String], requestShutdown: Future[Unit]): fs2.Stream[Future, StreamApp.ExitCode] = {
//    val server = Await.result(httpServer[Future](Some(ServerConfiguration(8081, "localhost"))), Duration.Inf)
//    server.stream
//  }
}
