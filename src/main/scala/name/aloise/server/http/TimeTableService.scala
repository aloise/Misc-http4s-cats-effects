package name.aloise.server.http

import java.time.LocalTime

import cats.data.{Validated, ValidatedNel}
import cats.effect.Effect
import io.circe.{Encoder, Json, JsonNumber}
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import name.aloise.data._
import name.aloise.server.http.response.Error
import name.aloise.service.TransportationTimeTableService
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import name.aloise.server.http.request.Implicits._

class TimeTableService[F[_]: Effect](timeTableService:TransportationTimeTableService[F]) extends Http4sDsl[F] {
  import org.http4s.circe.CirceEntityEncoder._
  import TimeTableService._
  import io.circe.generic.auto._

  val service: HttpService[F] = HttpService[F] {

      case GET -> Root / "lines" :? PositionXParam(x) +& PositionYParam(y) +& TimestampParam(time) =>
        for {
          lineItemsFound <- timeTableService.getLinesAtTimeAndPosition(time, GridPosition(x,y))
          response <- Ok(GetLinesResponse(lineItemsFound))
        } yield response

      case GET -> Root / "lines" / lineNameStr =>
        for {
          lineNameOpt <- timeTableService.getLineByName(lineNameStr)
          response <- lineNameOpt.fold(NotFound(Error("Line was not found by name"))) { lineName =>
            timeTableService.isDelayed(lineName).flatMap(isDelayed => Ok(LineDelayed(lineName, isDelayed)))
          }
        } yield response

      case GET -> Root / "lines" =>
        NotAcceptable(Error("lines request requires query params"))

    }
}

object TimeTableService {

  object PositionXParam extends QueryParamDecoderMatcher[PositionX]("x")
  object PositionYParam extends QueryParamDecoderMatcher[PositionY]("y")
  object TimestampParam extends QueryParamDecoderMatcher[LocalTime]("timestamp")

  implicit val localTimeToCirce:Encoder[LocalTime] = (a: LocalTime) => Json.fromString(a.toString)

  sealed trait Response
  case class GetLinesResponse(lines:Set[LineName]) extends Response
  case class LineDelayed(lineName:LineName, isDelayed:Boolean) extends Response


}
