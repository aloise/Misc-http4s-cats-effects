package com.mobimeo

import java.time.LocalTime

import scala.concurrent.duration.FiniteDuration

package object datasource {

  sealed trait Position {
    def position: Int
  }

  case class LineId(id: Int) extends AnyVal

  case class LineName(name: String) extends AnyVal

  case class StopId(id: Int) extends AnyVal

  case class PositionX(position: Int) extends AnyVal

  case class PositionY(position: Int) extends AnyVal

  // csv data model
  final case class Delay(lineName: LineName, delay: FiniteDuration)

  final case class Line(lineId: LineId, lineName: LineName)

  final case class StopPosition(stopId: StopId, x: PositionX, y: PositionY)

  final case class TimeTable(lineId: LineId, stopId: StopId, time: LocalTime)

}
