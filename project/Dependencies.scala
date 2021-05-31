import sbt._

object Dependencies {
  object Versions {
    val http4s           = "1.0.0-M23"
    val circe            = "0.14.1"
    val catsCore         = "2.3.0"
    val catsEffect       = "3.1.1"
    val catsRetry        = "3.0.0"
    val fs2              = "3.0.4"
    val squants          = "1.6.0" 
    val log4Cats         = "2.1.1"
    val newType          = "0.4.4"
    val kindProjector    = "0.13.0"
    val betterMonadicFor = "0.3.1"
  }

  private def http4s(artifact: String): ModuleID                = "org.http4s" %% s"http4s-$artifact" % Versions.http4s
  private def log4cats(artifact: String): ModuleID              = "org.typelevel" %% s"log4cats-$artifact" % Versions.log4Cats
  private def cats(artifact: String, version: String): ModuleID = "org.typelevel" %% s"cats-$artifact" % version
  private def circe(artifact: String): ModuleID                 = "io.circe" %% s"circe-$artifact" % Versions.circe

  object Libraries {
    //Http4s
    val Http4sBlazeServer = http4s("blaze-server")
    val Http4sBlazeClient = http4s("blaze-client")
    val Http4sCirce = http4s("http4s-circe")
    val Http4s = http4s("dsl")

    //Circe
    val CirceCore = circe("core")
    val CirceGeneric = circe("generic")
    val CirceParser = circe("parser")

    //Cats
    val CatsCore = cats("core", Versions.catsCore)
    val CatsEffect = cats("effect", Versions.catsEffect)
    val CatsRetry = "com.github.cb372" %% "cats-retry" % Versions.catsRetry

    //FS2
    val FS2 = "co.fs2" %% "fs2-core" % Versions.fs2

    //Logger
    val Log4CatsCore = log4cats("core")
    val Log4CatSslf4j = log4cats("slf4j")
    
    //NewType
    val NewType = "io.estatico" %% "newtype" % Versions.newType

    //Squants
    val Squants = "org.typelevel" %% "squants" % Versions.squants
  }

  object CompilerPlugin {
    val kindProjector = compilerPlugin(
      "org.typelevel" %% "kind-projector" % Versions.kindProjector cross CrossVersion.full
    )

    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
    )
  }
}
