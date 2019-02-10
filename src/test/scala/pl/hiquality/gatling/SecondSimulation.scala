package pl.hiquality.gatling

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class SecondSimulation extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:8080")

  val userId = new AtomicInteger(0)

  private val scn = scenario("Basic Scenario")
    .exec(http("Entrypoint")
      .get("/")
    )
    .exec(session => session.set("userId", userId.incrementAndGet.toString))
    .exec(http("Create user")
      .post("/users")
      .body(StringBody( session =>
        s"""{
          |"username":"${session("userId").as[String]}",
          |"password":"${session("userId").as[String]}",
          |"email":"kkk@kkk",
          |"age":33
          |}""".stripMargin)).asJson
    )
    .exec(http("Token")
      .get(session => s"/users/${session("userId").as[String]}/token")
      .basicAuth(session => session("userId").as[String], session => session("userId").as[String])
      .check(jsonPath("$").saveAs("userToken"))
    )
    .exec(
      http("Add Book")
      .post("/book")
        .header("Authorization", session => "Bearer " + session("userToken").as[String] )
        .body(StringBody(
      """{
        |"isbn":"123456",
        |"title":"Pan Tadeusz",
        |"authors":["Adam Mickiewicz"],
        |"page_count":888,
        |"public": true,
        |"owner":"kkk"
        |}""".stripMargin)).asJson
    )
    .exec(http("Book")
      .get("/book")
    )

  setUp(
    scn.inject(
      //atOnceUsers(100)
      rampUsers(100) during 15.seconds
    )
  ).protocols(httpProtocol)

}
