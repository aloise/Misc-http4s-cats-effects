import cats.effect.IO
import com.github.agourlay.cornichon.CornichonFeature
import org.scalatest.BeforeAndAfterAll

class DefaultServerSpec extends BaseServerSpec {

  def feature = Feature("Running DefaultServerSpec"){
    Scenario("Service is healthy"){

      When I get("/health")

      Then assert status.is(200)
      And assert body.whitelisting.is("""{"message":"Hello, aloise"}""")
    }
  }

}