package name.aloise.server.http

import java.time.LocalDateTime
import cats.implicits._
import cats.effect.Effect
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl
import io.circe.java8.time._

class HealthService[F[_]: Effect] extends Http4sDsl[F] {
  import org.http4s.circe.CirceEntityEncoder._
  import HealthService._
  import io.circe.generic.auto._

  val service: HttpService[F] = HttpService[F] {
    case GET -> Root =>
      Ok(HealthResponse(LocalDateTime.now(), "healthy"))
  }
}

object HealthService {
  sealed trait Response
  case class HealthResponse(time:LocalDateTime, status:String) extends Response

}
