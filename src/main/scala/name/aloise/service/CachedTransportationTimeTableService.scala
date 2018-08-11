package name.aloise.service

import java.time.LocalTime

import cats.effect.{Effect, IO}
import name.aloise.data
import name.aloise.data._
import name.aloise.datasource._
import scalacache._
import scalacache.memoization._
import scala.concurrent.duration._
import scalacache.caffeine._
import scalacache.CatsEffect.modes._

final case class CachedTransportationTimeTableService[F[_] : Effect](datasource: TransportationDatasource[F]) extends TransportationTimeTableService[F](datasource) {

  private implicit val linesByNameFCaffeineCache: Cache[Map[LineName, Line]] = CaffeineCache[Map[LineName, Line]]
  private implicit val linesByIdFCaffeineCache: Cache[Map[LineId, Line]] = CaffeineCache[Map[LineId, Line]]
  private implicit val stopsOnPositionFCaffeineCache: Cache[Map[(PositionX, PositionY), Set[StopId]]] = CaffeineCache[Map[(PositionX, PositionY), Set[StopId]]]
  private implicit val delayByLineIdFCaffeineCache: Cache[Map[LineId, FiniteDuration]] = CaffeineCache[Map[LineId, FiniteDuration]]

  private def linesByNameF: F[Map[LineName, Line]] = memoizeF(None) {
    eff.map(datasource.getLines)(lines => lines.map(l => l.lineName -> l).toMap)

  }
  private def linesByIdF: F[Map[LineId, Line]] = memoizeF(None){
    eff.map(datasource.getLines)(lines => lines.map(l => l.lineId -> l).toMap)
  }
  private def stopsOnPositionF: F[Map[(PositionX, PositionY), Set[StopId]]] = memoizeF(None) {
    eff.map(datasource.getStops)(stops => stops.groupBy(s => (s.x, s.y)).mapValues(_.map(_.stopId).toSet))
  }
  private def delayByLineIdF: F[Map[LineId, FiniteDuration]] = memoizeF(None){
    eff.map(eff.product(datasource.getDelays, linesByNameF)) { case (delays, linesByNameData) =>
      delays.flatMap(d => linesByNameData.get(d.lineName).map(_.lineId -> d.delay)).toMap
    }
  }

  private def searchStops(
                           searchTime: LocalTime,
                           delayByLineId: Map[LineId, FiniteDuration],
                           stopsOnPosition: Set[StopId]
                         )(t: TimeTable): Boolean =
    (searchTime == t.time.plusNanos(delayByLineId.getOrElse(t.lineId, 0.seconds).toNanos)) && stopsOnPosition.contains(t.stopId)

  override def getLinesAtTimeAndPosition(searchTime: LocalTime, position: data.GridPosition): F[Set[data.LineName]] = eff.map4(linesByIdF, stopsOnPositionF, delayByLineIdF, datasource.getTimes) { case (linesById, stopsOnPositionMap, delayByLineId, timeTable) =>

    val stopsOnPosition = stopsOnPositionMap.getOrElse((position.x, position.y), Set.empty)
    val timeTableWithDelays = timeTable.filter(searchStops(searchTime, delayByLineId, stopsOnPosition))

    timeTableWithDelays.flatMap(t => linesById.get(t.lineId).map(_.lineName)).toSet
  }

  override def isDelayed(lineName: data.LineName): F[Boolean] = eff.map2(delayByLineIdF, linesByNameF) { (delayed, linesByName) =>
    linesByName.get(lineName).flatMap { line =>
      delayed.get(line.lineId)
    }.exists(_.toNanos > 0)
  }

  override def getLineByName(lineNameStr: String): F[Option[data.LineName]] = eff.map(linesByNameF) { lineNames =>
    lineNames.get(LineName(lineNameStr)).map(_.lineName)
  }
}