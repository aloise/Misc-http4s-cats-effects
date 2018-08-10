import cats._
import cats.implicits._
import cats.effect.IO
import com.github.agourlay.cornichon.CornichonFeature
import com.mobimeo.datasource.csv.CsvTransportationDatasource
import com.mobimeo.server.{DefaultServerBuilder, ServerConfiguration}
import com.mobimeo.server.http.DefaultHttpService
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
    // accessing baseUrl to make sure the server was initialized
    val _ = baseUrl
    println(baseUrl)
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    serverInstance.foreach( _.shutdown.unsafeRunSync())
    serverInstance = None
  }
}