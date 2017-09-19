package org.tzotopia.keycloak.akkahttp

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.softwaremill.sttp._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by Radu Toev on 19.09.2017.
  */
class FirstSpec extends WordSpec with Matchers with ScalatestRouteTest {
  "An authenticated user" should {
    "access a secured endpoint" in {
      //get a token
      implicit val backend = HttpURLConnectionBackend()
      implicit val formats = DefaultFormats

      val r = sttp.post(uri"http://localhost:8080/auth/realms/AkkaHttpIntegration/protocol/openid-connect/token")
          .headers(Map("Content-Type" -> "application/x-www-form-urlencoded"))
          .body(
            "grant_type" -> "password",
            "username" -> "myuser",
            "password" -> "pass",
            "client_id" -> "public-client"
          ).mapResponse(re => {
            parse(re.toString).mapField {
              case (key, JString(value)) => (key, JString(value.toString))
              case x => x
            }.extract[Map[String, String]]
          })
          .send()

      val accessToken = r.body.right.get("access_token")
    }
  }
}