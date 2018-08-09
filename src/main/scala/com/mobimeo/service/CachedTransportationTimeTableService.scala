package com.mobimeo.service

import java.time.LocalTime

import cats.effect.Effect
import com.mobimeo.{data, datasource}
import com.mobimeo.datasource.{Line, TimeTable, TransportationDatasource}
import cats.implicits._

import scala.concurrent.duration._

/**
  * Simplest Possible functionally pure implementation and ridiculously inefficient implementation. There are no optimizations intentionally therefore it's computationally inefficient. Practically it should use both IntervalTree to optimize memory interval calculations and KD-tree to get positions quickly.
  * @param ds Datasource
  * @tparam F Async Effect
  */
final case class CachedTransportationTimeTableService[F[_]:Effect](datasource: TransportationDatasource[F]) extends TransportationTimeTableService[F](datasource) {


  override def getLinesAtTimeAndPosition(searchTime: LocalTime, position: data.GridPosition): F[List[data.LineName]] =
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
    } yield timeTableWithDelays.flatMap(t => linesById.get(t.lineId))


  private def searchStops(
                           searchTime: LocalTime,
                           delayByLineId: Map[data.LineId, FiniteDuration],
                           stopsOnPosition: Set[data.StopId]
                         )(t:TimeTable) =
      (searchTime == t.time.plusNanos(delayByLineId.getOrElse(t.lineId, 0.seconds).toNanos)) &&
      stopsOnPosition.contains(t.stopId)

  override def isDelayed(time: LocalTime, lineName: data.LineName): F[Boolean] =
    for {
      delays <- datasource.getDelays
      // it's not delayed in two cases : not found in the delay table OR current delay duration is zero
      isDelayed = delays.find(_.lineName == lineName).exists(_.delay.length == 0)
    } yield isDelayed

  override def getLineByName(lineNameStr: String): F[data.LineName] =
    for {
      lines <- datasource.getLines
      lineNameOpt = lines.find(l => l.lineName.name == lineNameStr).toRight(LineNotFoundByName(lineNameStr))
      result <- eff.fromEither(lineNameOpt.map(_.lineName))
    } yield result
}
