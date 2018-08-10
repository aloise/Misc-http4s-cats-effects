import java.time.LocalTime

import cats.effect._
import cats.implicits._
import cats.effect.implicits._
import com.mobimeo.data._
import com.mobimeo.datasource._
import com.mobimeo.service.AsyncTransportationTimeTableService
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.duration._

class TransportationTimeTableServiceSpec extends WordSpec with MustMatchers {

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

  "A time table service should" should {
    "not be delayed for m1" in {
      getService.isDelayed(m1).unsafeRunSync must be(false)
    }
    "be delayed fo s5" in {
      getService.isDelayed(s5).unsafeRunSync must be(true)
    }
    "have m1 and s5 on stop2 at 09:10:00" in {
      getService
        .getLinesAtTimeAndPosition(LocalTime.parse("09:10:00"), GridPosition(PositionX(2),PositionY(2)))
        .unsafeRunSync must be (Set(m1,s5))
    }
    "have m1 on stop1 at 09:01:00 on stop1" in {
      getService
        .getLinesAtTimeAndPosition(LocalTime.parse("09:01:00"), GridPosition(PositionX(1),PositionY(1)))
        .unsafeRunSync must be (Set(s5))
    }
    "have m1 on stop4 at 09:20:00 on stop4" in {
      getService
        .getLinesAtTimeAndPosition(LocalTime.parse("09:20:00"), GridPosition(PositionX(4),PositionY(4)))
        .unsafeRunSync must be (Set(m1))
    }
    "not have anything at 08:00:00 on stop1" in {
      getService
        .getLinesAtTimeAndPosition(LocalTime.parse("09:00:00"), GridPosition(PositionX(1),PositionY(1)))
        .unsafeRunSync must be (Set.empty)
    }
    "not have anything at 09:00:00 somewhere out of the city :)" in {
      getService
        .getLinesAtTimeAndPosition(LocalTime.parse("09:00:00"), GridPosition(PositionX(100),PositionY(100)))
        .unsafeRunSync must be (Set.empty)
    }
    // and some property tests
  }

}
