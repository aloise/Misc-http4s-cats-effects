package com.mobimeo

import java.time.LocalTime

import com.mobimeo.data._

import scala.concurrent.duration.FiniteDuration

package object datasource {

  // csv data model
  case class Delay(lineName: LineName, delay: FiniteDuration)

  case class Line(lineId: LineId, lineName: LineName)

  case class StopPosition(stopId: StopId, x: PositionX, y: PositionY)

  case class TimeTable(lineId: LineId, stopId: StopId, time: LocalTime)

}
