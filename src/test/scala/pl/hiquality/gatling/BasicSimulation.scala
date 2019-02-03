package pl.hiquality.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:8080")

  private val scn = scenario("Basic Scenario")
    .exec(http("Entrypoint").get("/"))
    .pause(5 seconds)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
