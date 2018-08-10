import cats.effect.Effect
import com.aloise.datasource._

case class MemoryDatasource[F[_] : Effect](
                                            delays: List[Delay],
                                            lines: List[Line],
                                            stops: List[StopPosition],
                                            times: List[TimeTable]
                                          ) extends TransportationDatasource[F] {

  def getDelays: F[List[Delay]] = eff[F].pure(delays)

  def getLines: F[List[Line]] = eff[F].pure(lines)

  def getStops: F[List[StopPosition]] = eff[F].pure(stops)

  def getTimes: F[List[TimeTable]] = eff[F].pure(times)
}