package com.mobimeo

import java.time.LocalTime

import com.mobimeo.data._

import scala.concurrent.duration.FiniteDuration

package object datasource {

  // csv data model
  final case class Delay(lineName: LineName, delay: FiniteDuration)

  final case class Line(lineId: LineId, lineName: LineName)

  final case class StopPosition(stopId: StopId, x: PositionX, y: PositionY)

  final case class TimeTable(lineId: LineId, stopId: StopId, time: LocalTime)

}
