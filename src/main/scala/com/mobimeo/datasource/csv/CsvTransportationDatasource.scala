package com.mobimeo.datasource.csv

import cats.effect.Effect
import com.mobimeo.datasource._
import com.typesafe.scalalogging.StrictLogging
import kantan.csv.CsvConfiguration.Header.Implicit

import scala.concurrent.duration._
import scala.language.higherKinds


case class CsvTransportationDatasource[F[_] : Effect](
                                                       delaysSource: F[String],
                                                       linesSource: F[String],
                                                       stopsSource: F[String],
                                                       timesSource: F[String]
                                                     ) extends TransportationDatasource[F] with StrictLogging {

  import kantan.csv._
  import kantan.csv.ops._
  import kantan.csv.HeaderDecoder.defaultHeaderDecoder
  import kantan.csv.generic._
  import kantan.csv.java8.defaultLocalTimeCellDecoder

  // it would skip csv error processing as of now
  override lazy val getDelays: F[List[Delay]] =
    readLinesSkippingErrors[Delay](delaysSource)

  private implicit val rowDecoder: RowDecoder[FiniteDuration] = implicitly[RowDecoder[Int]].map(minutes => FiniteDuration(minutes, MINUTES))
  override lazy val getLines: F[List[Line]] =
    readLinesSkippingErrors[Line](linesSource)
  override lazy val getStops: F[List[StopPosition]] =
    readLinesSkippingErrors[StopPosition](stopsSource)
  override lazy val getTimes: F[List[TimeTable]] =
    readLinesSkippingErrors[TimeTable](timesSource)
  private val csvConfig = rfc.copy(header = Implicit)

  def readLinesSkippingErrors[X: HeaderDecoder](datasource: F[String]): F[List[X]] =
    eff.map(datasource)(_.asCsvReader[X](csvConfig).toList.flatMap { x =>
      x.left.foreach { err =>
        logger.error("Error parsing CSV Datasource row", err)
      }
      x.toOption
    })
}
