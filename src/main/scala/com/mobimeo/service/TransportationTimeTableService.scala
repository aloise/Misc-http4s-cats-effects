package com.mobimeo.service

import java.time.LocalTime

import cats.effect.Effect
import com.mobimeo.data.{GridPosition, LineName}
import com.mobimeo.datasource.TransportationDatasource
import com.mobimeo.utils.WithEffects

abstract class TransportationTimeTableService[F[_] : Effect](ds: TransportationDatasource[F]) extends WithEffects {

  def getLinesAtTimeAndPosition(time: LocalTime, position: GridPosition): F[List[LineName]]

  def isDelayed(time: LocalTime, lineName: LineName): F[Boolean]

  def getLineByName(lineNameStr: String): LineName

}
