import cats.effect.IO
import com.github.agourlay.cornichon.CornichonFeature
import org.scalatest.BeforeAndAfterAll

class DefaultServerSpec extends BaseServerSpec {

  def feature = Feature("Running DefaultServerSpec"){

    Scenario("line was delayed"){
      When I get("/lines/S75")
      Then assert status.is(200)
      Then assert body.is(
        """{
          "isDelayed": true,
          "lineName" : { "name" : "S75" }
        }""")
    }

    Scenario("find no lines at time 10:00:00 and location (1,1)"){
      When I get("/lines?x=1&y=1&timestamp=10:00:00")
      Then assert status.is(200)
      Then assert body.is(
        """{
           "lines": []
        }""")
    }

    Scenario("find delayed M4 at time 10:01:00 and location (1,1)"){
      When I get("/lines?x=1&y=1&timestamp=10:01:00")
      Then assert status.is(200)
      Then assert body.is(
        """{
           "lines": [{"name":"M4"}]
        }""")
    }

    // more scenarios here
  }

}