package com.mobimeo.server.http

import java.time.LocalTime

import cats.data.{Validated, ValidatedNel}
import cats.effect.Effect
import io.circe.{Encoder, Json, JsonNumber}
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import com.mobimeo.data._
import com.mobimeo.server.http.response.Error
import com.mobimeo.service.TransportationTimeTableService
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import com.mobimeo.server.http.request.Implicits._

class TimeTableService[F[_]: Effect](timeTableService:TransportationTimeTableService[F]) extends Http4sDsl[F] {
  import org.http4s.circe.CirceEntityEncoder._
  import TimeTableService._
  import io.circe.generic.auto._

  val service: HttpService[F] = HttpService[F] {

      case GET -> Root / "lines" :? PositionXParam(x) +& PositionYParam(y) +& TimestampParam(time) =>
        Ok(GetLinesResponse(List.empty))

      case GET -> Root / "lines" =>
        NotAcceptable(Error("lines request requires query params x,y and time"))
    }
}

object TimeTableService {

  object PositionXParam extends QueryParamDecoderMatcher[PositionX]("x")
  object PositionYParam extends QueryParamDecoderMatcher[PositionY]("y")
  object TimestampParam extends QueryParamDecoderMatcher[LocalTime]("timestamp")

  implicit val localTimeToCirce:Encoder[LocalTime] = (a: LocalTime) => Json.fromString(a.toString)

  sealed trait Response
  case class GetLinesResponse(lines:List[LineName])


}
