import sbt._

object Dependencies {
  object Versions {
    val http4s           = "1.0.0-M22"
    val http4sJwtAuth    = "1.0.0-RC3"
    val circe            = "0.14.1"
    val catsCore         = "2.3.0"
    val catsEffect       = "3.1.1"
    val catsRetry        = "3.0.0"
    val fs2              = "3.0.4"
    val squants          = "1.6.0"
    val log4Cats         = "2.1.1"
    val derevo           = "0.12.5"
    val newType          = "0.4.4"
    val kindProjector    = "0.13.0"
    val betterMonadicFor = "0.3.1"
    val refined          = "0.9.26"
    val skunk            = "0.2.0"
  }

  //"tf.tofu" %% "derevo-cats" % "latest version in badge"

  private def http4s(artifact: String): ModuleID                = "org.http4s" %% s"http4s-$artifact" % Versions.http4s
  private def log4cats(artifact: String): ModuleID              = "org.typelevel" %% s"log4cats-$artifact" % Versions.log4Cats
  private def cats(artifact: String, version: String): ModuleID = "org.typelevel" %% s"cats-$artifact" % version
  private def circe(artifact: String): ModuleID                 = "io.circe" %% s"circe-$artifact" % Versions.circe
  private def refined(artifact: String = ""): ModuleID          = "eu.timepit" %% s"refined$artifact" % Versions.refined
  private def derevo(artifact: String): ModuleID                = "tf.tofu" %% s"derevo-$artifact" % Versions.derevo

  object Libraries {
    //Http4s
    val Http4sBlazeServer = http4s("blaze-server")
    val Http4sBlazeClient = http4s("blaze-client")
    val Http4sCirce       = http4s("circe")
    val Http4s            = http4s("dsl")

    //Circe
    val CirceCore    = circe("core")
    val CirceGeneric = circe("generic")
    val CirceParser  = circe("parser")
    val CirceRefined = circe("refined")

    //Cats
    val CatsCore   = cats("core", Versions.catsCore)
    val CatsEffect = cats("effect", Versions.catsEffect)
    val CatsRetry  = "com.github.cb372" %% "cats-retry" % Versions.catsRetry

    //Refined
    val RefinedCore = refined()
    val RefinedCats = refined("-cats")

    //Logger
    val Log4CatsCore  = log4cats("core")
    val Log4CatSslf4j = log4cats("slf4j")

    //Derevo
    val DerevoCore          = derevo("core")
    val DerevoCats          = derevo("cats")
    val DerevoCirce         = derevo("circe")
    val DerevoCirceMagnolia = derevo("circe-magnolia")

    //Htt4s JWT Auth
    val Http4sJwtAuth = "dev.profunktor" %% "http4s-jwt-auth" % Versions.http4sJwtAuth

    //FS2
    val FS2 = "co.fs2" %% "fs2-core" % Versions.fs2

    //NewType
    val NewType = "io.estatico" %% "newtype" % Versions.newType

    //Squants
    val Squants = "org.typelevel" %% "squants" % Versions.squants

    //Skunk
    val Skunk = "org.tpolecat" %% "skunk-core" % Versions.skunk
  }

  object CompilerPlugin {
    val kindProjector = compilerPlugin(
      "org.typelevel" %% "kind-projector" % Versions.kindProjector cross CrossVersion.full
    )

    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
    )

    val macros = addCompilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
    )
  }
}
