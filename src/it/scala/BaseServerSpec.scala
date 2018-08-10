import cats._
import cats.implicits._
import cats.effect.IO
import com.github.agourlay.cornichon.CornichonFeature
import com.aloise.datasource.csv.CsvTransportationDatasource
import com.aloise.server.{DefaultServerBuilder, ServerConfiguration}
import com.aloise.server.http.DefaultHttpService
import com.typesafe.scalalogging.StrictLogging
import org.http4s.server.Server
import org.scalatest.BeforeAndAfterAll

trait BaseServerSpec extends CornichonFeature with BeforeAndAfterAll with DefaultServerBuilder {
  import scala.concurrent.ExecutionContext.Implicits.global

  private var serverInstance:Option[Server[IO]] = None

  def server: Server[IO] = Option(serverInstance).flatten.getOrElse{
    val srv = httpServer(IO.pure(ServerConfiguration(0,"localhost")))
    serverInstance = Some(srv.unsafeRunSync.start.unsafeRunSync)
    serverInstance.get
  }

  override lazy val baseUrl = server.baseUri.toString()

  override def beforeAll(): Unit = {
    // accessing baseUrl to make sure the server was initialized - that's how Cornichon library is designed :(
    val _ = baseUrl
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    serverInstance.foreach( _.shutdown.unsafeRunSync())
    serverInstance = None
  }
}