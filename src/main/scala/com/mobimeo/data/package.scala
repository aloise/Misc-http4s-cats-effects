package com.mobimeo

package object data {

  sealed trait Position extends Any {
    def position: Int
  }

  case class PositionX(position: Int) extends AnyVal with Position

  case class PositionY(position: Int) extends AnyVal with Position

  case class GridPosition(x: PositionX, y: PositionY)

  case class LineId(id: Int) extends AnyVal

  case class LineName(name: String) extends AnyVal

  case class StopId(id: Int) extends AnyVal

}
