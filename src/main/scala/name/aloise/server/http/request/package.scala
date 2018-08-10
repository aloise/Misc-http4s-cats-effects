package name.aloise.server.http

import java.time.LocalTime

import name.aloise.data.{LineName, PositionX, PositionY}
import org.http4s.{ParseResult, QueryParamDecoder, QueryParameterValue}
import cats.implicits._

package object request {

  object Implicits {
    implicit val positionXParamDecoder: QueryParamDecoder[PositionX] = QueryParamDecoder[Int].map(PositionX)
    implicit val positionYParamDecoder: QueryParamDecoder[PositionY] = QueryParamDecoder[Int].map(PositionY)
    implicit val lineNameParamDecoder: QueryParamDecoder[LineName] = QueryParamDecoder[String].map(LineName)
    implicit val localTimeQueryParamDecoder:QueryParamDecoder[LocalTime] = (value: QueryParameterValue) =>
      ParseResult
        .fromTryCatchNonFatal("Failed to parse Time")(LocalTime.parse(value.value))
        .toValidatedNel
  }
}
