package name.aloise.server

import cats.effect.{Effect, IO}
import name.aloise.datasource.csv.CsvTransportationDatasource
import name.aloise.server.http.DefaultHttpService
import name.aloise.service._
import name.aloise.utils.WithEffects

import scala.concurrent.ExecutionContext
import scala.language.higherKinds
import scala.util.Try

trait DefaultServerBuilder extends WithEffects {

  /**
    * Composing server instance
    * @return
    */
  protected def httpServer[IOEffect[_]:Effect](getConfig:Option[ServerConfiguration] = None): IOEffect[DefaultHttpService[IOEffect]] = {

    def loadConfig:IOEffect[ServerConfiguration] =
      eff[IOEffect].pure(pureconfig.loadConfigOrThrow[ServerConfiguration]("server"))

    eff[IOEffect].map( getConfig.map(eff[IOEffect].pure) getOrElse loadConfig ){ config =>
      val datasource = CsvTransportationDatasource.fromResources[IOEffect]()
      val timeTableService = CachedTransportationTimeTableService[IOEffect](datasource)
      DefaultHttpService[IOEffect](config)(timeTableService)
    }

  }

}
