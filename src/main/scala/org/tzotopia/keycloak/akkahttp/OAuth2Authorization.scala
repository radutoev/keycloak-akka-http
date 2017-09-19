package org.tzotopia.keycloak.akkahttp

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import org.keycloak.representations.AccessToken

/**
  * Created by Radu Toev on 19.09.2017.
  */
class OAuth2Authorization(tokenVerifier: KeycloakTokenVerifier) {
  def authenticated: Directive1[OAuth2Token] = {
    bearerToken.flatMap {
      case Some(token) => {
        onComplete(tokenVerifier.verifyToken(token)).flatMap {
          _.map(accessToken => provide(OAuth2Token(accessToken)))
              .recover {
                case x =>
                  print(x)
                  reject(AuthorizationFailedRejection).toDirective[Tuple1[OAuth2Token]]
              }
              .get
        }
      }
      case None => reject(AuthorizationFailedRejection)
    }
  }

  private def bearerToken: Directive1[Option[String]] = {
    for {
      authBearerToken <- optionalHeaderValueByType(classOf[Authorization]).map(extractBearerToken)
    } yield authBearerToken.orElse(None)
  }

  private def extractBearerToken(authHeader: Option[Authorization]): Option[String] = {
    authHeader.collect {
      case Authorization(OAuth2BearerToken(token)) => token
    }
  }
}

case class OAuth2Token(token: AccessToken)