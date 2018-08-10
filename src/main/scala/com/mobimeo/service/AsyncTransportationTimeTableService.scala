package com.mobimeo.service

import java.time.LocalTime

import cats.effect.Effect
import com.mobimeo.data._
import com.mobimeo.datasource.{Line, TimeTable, TransportationDatasource}
import cats.implicits._

import scala.concurrent.duration._

/**
  * Simplest Possible functionally pure implementation and ridiculously inefficient implementation. There are no optimizations intentionally therefore it's computationally inefficient. Practically it should use both IntervalTree to optimize memory interval calculations and KD-tree to get positions quickly.
  * @param ds Datasource
  * @tparam F Async Effect
  */
final case class AsyncTransportationTimeTableService[F[_]:Effect](datasource: TransportationDatasource[F]) extends TransportationTimeTableService[F](datasource) {

  override def getLinesAtTimeAndPosition(searchTime: LocalTime, position: GridPosition): F[Set[LineName]] =
    for {
      lines <- datasource.getLines
      linesByName = lines.map(l => l.lineName -> l.lineId).toMap
      linesById = lines.map(l => l.lineId -> l.lineName).toMap
      positions <- datasource.getStops
      stopsOnPosition = positions.filter(p => p.x == position.x && p.y == position.y ).map(_.stopId).toSet
      delays <- datasource.getDelays
      delayByLineId = delays.flatMap(d => linesByName.get( d.lineName ).map( _ -> d.delay ) ).toMap
      timeTable <- datasource.getTimes
      timeTableWithDelays = timeTable.filter(searchStops(searchTime, delayByLineId, stopsOnPosition))
    } yield timeTableWithDelays.flatMap(t => linesById.get(t.lineId)).toSet


  private def searchStops(
                           searchTime: LocalTime,
                           delayByLineId: Map[LineId, FiniteDuration],
                           stopsOnPosition: Set[StopId]
                         )(t:TimeTable) =
      (searchTime == t.time.plusNanos(delayByLineId.getOrElse(t.lineId, 0.seconds).toNanos)) &&
      stopsOnPosition.contains(t.stopId)

  override def isDelayed(lineName: LineName): F[Boolean] =
    for {
      delays <- datasource.getDelays
      // it's not delayed in two cases : not found in the delay table OR current delay duration is zero
      isDelayed = delays.find(_.lineName == lineName).exists(_.delay.length > 0)
    } yield isDelayed

  override def getLineByName(lineNameStr: String): F[Option[LineName]] =
    for {
      lines <- datasource.getLines
      lineNameOpt = lines.find(l => l.lineName.name == lineNameStr).map(_.lineName)
    } yield lineNameOpt
}
