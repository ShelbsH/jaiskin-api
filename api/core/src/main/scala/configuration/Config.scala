package configuration

import ciris.{ env }
import ciris.refined._
import ciris.ConfigDecoder
import cats.implicits._
import cats.effect.Async
import eu.timepit.refined.auto._
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.types.string.NonEmptyString
import scala.concurrent.duration._

import configuration.types._

object Config {
  //TODO: Move the decoder into a separate file
  implicit val jwtConfigDecoder: ConfigDecoder[String, JwtSecretKeyConfig] =
    ConfigDecoder[String, NonEmptyString].map(JwtSecretKeyConfig(_))

  def load[F[_]: Async]: F[AppConfig] =
    (
      env("JAISKIN_POSTGRES_PASSWORD").as[NonEmptyString].secret,
      env("JAISKIN_JWT_SECRET_KEY").as[JwtSecretKeyConfig].secret
    )
      .parMapN { (psqlPassword, jwtSecret) =>
        AppConfig(
          PostgresConfig(
            host = "localhost",
            user = "shelby",
            database = "jaiskin",
            password = psqlPassword,
            port = 5432,
            max = 10
          ),
          jwtSecret,
          JwtIssuer("jaiskin"),
          JwtExpiration(15.minutes)
        )
      }
      .load[F]
}
