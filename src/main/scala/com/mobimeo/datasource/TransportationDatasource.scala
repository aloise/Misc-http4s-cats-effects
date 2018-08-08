package com.mobimeo.datasource

import cats.effect.Effect

import scala.language.higherKinds

abstract class TransportationDatasource[F[_] : Effect] {

  def getDelays: F[List[Delay]]

  def getLines: F[List[Line]]

  def getStops: F[List[StopPosition]]

  def getTimes: F[List[TimeTable]]

  // helper method
  protected def eff: Effect[F] = implicitly[Effect[F]]

}