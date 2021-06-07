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
      Libraries.CirceCore,
      Libraries.CirceGeneric,
      Libraries.CirceParser,
      Libraries.CatsCore,
      Libraries.CatsEffect,
      Libraries.CatsRetry,
      Libraries.FS2,
      Libraries.RefinedCore,
      Libraries.RefinedCats,
      Libraries.Log4CatsCore,
      Libraries.Log4CatSslf4j,
      Libraries.NewType,
      Libraries.Squants,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.kindProjector
    )
  )
