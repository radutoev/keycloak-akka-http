package org.tzotopia.keycloak.akkahttp

import java.util.concurrent.ForkJoinPool

import org.keycloak.RSATokenVerifier
import org.keycloak.adapters.KeycloakDeployment
import org.keycloak.representations.AccessToken

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Radu Toev on 19.09.2017.
  */
class KeycloakTokenVerifier(kId: String, keycloakDeployment: KeycloakDeployment) {
  implicit val executionContext = ExecutionContext.fromExecutor(new ForkJoinPool(2))

  def verifyToken(token: String): Future[AccessToken] = {
    Future {
      RSATokenVerifier.verifyToken(
        token,
        keycloakDeployment.getPublicKeyLocator
            .getPublicKey(kId, keycloakDeployment),
        keycloakDeployment.getRealmInfoUrl
      )
    }
  }
}
