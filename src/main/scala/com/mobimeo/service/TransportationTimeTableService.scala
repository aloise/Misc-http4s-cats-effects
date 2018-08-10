package com.mobimeo.service

import java.time.LocalTime

import cats.effect.Effect
import com.mobimeo.data.{GridPosition, LineName}
import com.mobimeo.datasource.TransportationDatasource
import com.mobimeo.utils.WithEffects

/**
  * Simple transporation info provide interface
  * @param ds Basic Datasource

  */
abstract class TransportationTimeTableService[F[_] : Effect](ds: TransportationDatasource[F]) extends WithEffects {

  def getLinesAtTimeAndPosition(time: LocalTime, position: GridPosition): F[Set[LineName]]

  def isDelayed(lineName: LineName): F[Boolean]

  def getLineByName(lineNameStr: String): F[Option[LineName]]

}

sealed abstract class TransportationTimeTableServiceError(msg:String) extends Exception(msg)
case class LineNotFoundByName(lineNameStr:String) extends TransportationTimeTableServiceError(s"Line not found by name: $lineNameStr")
