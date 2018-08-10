package com.mobimeo.server.http

import cats.Id
import cats.effect.{Effect, IO}
import com.mobimeo.server.ServerConfiguration
import com.mobimeo.service.TransportationTimeTableService
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

case class DefaultHttpService[F[_]: Effect](configuration:ServerConfiguration)(dataService:TransportationTimeTableService[F]) {

  private lazy val service =
    BlazeBuilder[F]
      .bindHttp(configuration.port, configuration.host)
      .mountService(new TimeTableService(dataService).service, "/")
      .mountService(new HealthService[F].service, "/health")
      .withoutBanner

  def stream(implicit ec: ExecutionContext) =
    service.serve

  def start = service.start
}
