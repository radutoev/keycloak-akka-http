name := "akka-http-keycloak"

version := "0.1"

scalaVersion := "2.12.3"

val akkaHttpV = "10.0.10"
val akkaV = "2.5.4"
val keycloakV = "3.2.0.Final"
val json4sV = "3.5.3"
val scalaTestV = "3.0.1"
val sttpV = "0.0.14"

val akka = Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpV,
  "com.typesafe.akka" %% "akka-stream" % akkaV ,
  "com.typesafe.akka" %% "akka-actor"  % akkaV,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV

)

val keycloak = Seq(
  "org.keycloak" % "keycloak-adapter-core" % keycloakV,
  "org.keycloak" % "keycloak-core" % keycloakV,
  "org.jboss.logging" % "jboss-logging" % "3.3.1.Final",
  "org.apache.httpcomponents" % "httpclient" % "4.5.3"
)

val json4s = Seq(
  "org.json4s" %% "json4s-native" % json4sV
)

val scalaTest = Seq(
  "org.scalatest" %% "scalatest" % scalaTestV % "test"
)

val sttp = Seq(
  "com.softwaremill.sttp" %% "core" % sttpV % "test"
)

libraryDependencies ++= akka ++ keycloak ++ json4s ++ scalaTest ++ sttp
