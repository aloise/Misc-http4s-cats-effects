package com.mobimeo.datasource

import cats.effect.Effect
import com.mobimeo.utils.WithEffects

import scala.language.higherKinds

/**
  * Abstract datasource - provides row data about stops, delays, etc
  */
abstract class TransportationDatasource[F[_] : Effect] extends WithEffects {

  def getDelays: F[List[Delay]]

  def getLines: F[List[Line]]

  def getStops: F[List[StopPosition]]

  def getTimes: F[List[TimeTable]]

}