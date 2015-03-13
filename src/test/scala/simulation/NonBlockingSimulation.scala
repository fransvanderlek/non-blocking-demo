package simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class NonBlockingSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://192.168.33.10:8082") // Here is the root for all relative URLs
    

  val nonblockScn = scenario("non-block") // A scenario is a chain of requests and pauses
    .exec(http("request_1")
      .get("/demo/invoke1"))
   

  setUp(
  	nonblockScn.inject( rampUsers(100) over (10 seconds) )
  	).protocols(httpConf) 
}