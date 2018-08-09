package com.mobimeo.datasource.csv

import cats.effect.{Effect, IO}
import com.mobimeo.datasource._
import com.typesafe.scalalogging.StrictLogging
import kantan.csv.CsvConfiguration.Header.Implicit

import scala.concurrent.duration._
import scala.io.Source
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

  override lazy val getDelays: F[List[Delay]] =
    readLinesSkippingErrors[Delay](delaysSource)

  private val csvConfig = rfc.copy(header = Implicit)

  private implicit val rowDecoder: RowDecoder[FiniteDuration] =
    implicitly[RowDecoder[Int]].map(minutes => FiniteDuration(minutes, MINUTES))
  override lazy val getLines: F[List[Line]] =
    readLinesSkippingErrors[Line](linesSource)
  override lazy val getStops: F[List[StopPosition]] =
    readLinesSkippingErrors[StopPosition](stopsSource)
  override lazy val getTimes: F[List[TimeTable]] =
    readLinesSkippingErrors[TimeTable](timesSource)

  // it would skip csv error processing as of now - logging only
  def readLinesSkippingErrors[X: HeaderDecoder](datasource: F[String]): F[List[X]] =
    eff.map(datasource)(_.asCsvReader[X](csvConfig).toList.flatMap { x =>
      x.left.foreach { err =>
        logger.error("Error parsing CSV Datasource row", err)
      }
      x.toOption
    })
}

object CsvTransportationDatasource {

  private def ioFromCSVDataResource[F[_] : Effect](filename: String): F[String] =
    implicitly[Effect[F]].pure(Source.fromResource("data/" + filename + ".csv").mkString)

  def fromResources[F[_]:Effect](
                                     delaysFilename:String ="delays",
                                     linesFilename:String = "lines",
                                     stopsFilename:String = "stops",
                                     timesFilename:String = "times"
                                   ):CsvTransportationDatasource[F] = CsvTransportationDatasource(
    ioFromCSVDataResource(delaysFilename),
    ioFromCSVDataResource(linesFilename),
    ioFromCSVDataResource(stopsFilename),
    ioFromCSVDataResource(timesFilename)
  )
}