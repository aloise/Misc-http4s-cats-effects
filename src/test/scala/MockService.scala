import java.time.LocalTime

import cats.effect.IO
import com.mobimeo.data._
import com.mobimeo.datasource.{Delay, Line, StopPosition, TimeTable}
import com.mobimeo.service.AsyncTransportationTimeTableService
import scala.concurrent.duration._

trait MockService {

  val l1 = LineId(1)
  val l2 = LineId(2)
  val s5 = LineName("S5")
  val m1 = LineName("M1")
  val lines = List(Line(l1, s5), Line(l2, m1))

  def getDatasource = {

    MemoryDatasource[IO](
      List(Delay(s5, 1.minute)),
      lines,
      List(
        StopPosition(StopId(1), PositionX(1), PositionY(1)),
        StopPosition(StopId(2), PositionX(2), PositionY(2)),
        StopPosition(StopId(3), PositionX(3), PositionY(3)),
        StopPosition(StopId(4), PositionX(4), PositionY(4))),
      List(
        TimeTable(l1, StopId(1), LocalTime.parse("09:00:00")), TimeTable(l1, StopId(2), LocalTime.parse("09:09:00")), TimeTable(l1, StopId(3), LocalTime.parse("09:20:00")),
        TimeTable(l2, StopId(2), LocalTime.parse("09:10:00")), TimeTable(l2, StopId(3), LocalTime.parse("09:10:00")), TimeTable(l2, StopId(4), LocalTime.parse("09:20:00"))
      )
    )
  }

  def getService = {
    AsyncTransportationTimeTableService(getDatasource)
  }

}
