import Dependencies._

val core = (project in file("api/core"))
  .settings(
    name := "api-jaiskin",
    organization := "shurns.dev",
    version := "0.1",
    scalaVersion := "2.13.6",
    scalacOptions ++= Seq(
      "-Ymacro-annotations",
      "-Ywarn-unused", //Temporary,
      "-Wconf:cat=unused:info",
      "-Ywarn-dead-code",
      "Ywarn-inaccessible",
      "-Ywarn-value-discard"
    ),
    libraryDependencies ++= Seq(
      Libraries.Http4s,
      Libraries.Http4sBlazeClient,
      Libraries.Http4sBlazeServer,
      Libraries.Http4sCirce,
      Libraries.CirceCore,
      Libraries.CirceGeneric,
      Libraries.CirceParser,
      Libraries.CirceRefined,
      Libraries.CatsCore,
      Libraries.CatsEffect,
      Libraries.CatsRetry,
      Libraries.FS2,
      Libraries.RefinedCore,
      Libraries.RefinedCats,
      Libraries.Log4CatsCore,
      Libraries.Log4CatSslf4j,
      Libraries.DerevoCore,
      Libraries.DerevoCats,
      Libraries.DerevoCirce,
      Libraries.DerevoCirceMagnolia,
      Libraries.Http4sJwtAuth,
      Libraries.NewType,
      Libraries.Squants,
      Libraries.Skunk,
      Libraries.Ciris,
      Libraries.CirisRefined,
      Libraries.CirisEnumeratum,
      Libraries.Monocle,
      Libraries.MonocleMacro,
      Libraries.TsecPassword,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.kindProjector
    )
  )
