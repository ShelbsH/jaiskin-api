import Dependencies._

val core = (project in file("api/core"))
  .settings(
    name := "api-jaiskin",
    organization := "shurns.dev",
    version := "0.1",
    scalaVersion := "2.13.6",
    scalacOptions += "-Ymacro-annotations",
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
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.kindProjector
    )
  )
