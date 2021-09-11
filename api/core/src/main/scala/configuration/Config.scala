package configuration

import ciris.{ env }
import ciris.refined._
import cats.implicits._
import cats.effect.Async
import eu.timepit.refined.auto._
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.types.string.NonEmptyString

import configuration.types.{ AppConfig, PostgresConfig }

object Config {
  def load[F[_]: Async]: F[AppConfig] =
    env("JAISKIN_POSTGRES_PASSWORD")
      .as[NonEmptyString]
      .secret
      .map { psqlPassword =>
        AppConfig(
          PostgresConfig(
            host = "localhost",
            user = "shelby",
            database = "jaiskin",
            password = psqlPassword,
            port = 5432,
            max = 10
          )
        )
      }
      .load[F]
}
