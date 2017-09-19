package org.tzotopia.keycloak.akkahttp

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import com.softwaremill.sttp._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.scalatest.{Matchers, WordSpec}
import Directives._
import akka.http.scaladsl.model.StatusCodes
import org.keycloak.adapters.KeycloakDeploymentBuilder

/**
  * Created by Radu Toev on 19.09.2017.
  */
class FirstSpec extends WordSpec with Matchers with ScalatestRouteTest {
  val oauth2 = new OAuth2Authorization(
    new KeycloakTokenVerifier(
      "g6Rf-QIkxtPwfHgUDymfz0FTh1efCkiBga4zzgtkrg8",
      KeycloakDeploymentBuilder.build(getClass.getResourceAsStream("/keycloak.json"))
    )
  )

  import oauth2._

  val testRoutes = {
    authenticated { token =>
      get {
        pathSingleSlash {
          complete {
            "ok"
          }
        }
      }
    }
  }

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

      Get() ~> addHeader("Authorization", s"Bearer $accessToken") ~> testRoutes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "ok"
      }
    }
  }

  "An unauthenticated user" should {
    "not be allowed to access a secured endpoint" in {
      Get() ~> testRoutes ~> check {
        rejection shouldEqual AuthorizationFailedRejection
      }
    }
  }
}
