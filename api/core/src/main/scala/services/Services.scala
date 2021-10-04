package services

import skunk.Session
import cats.effect.{ MonadCancelThrow, Resource }
import tsec.passwordhashers._
import tsec.passwordhashers.jca._

import tokens.Tokens
import crypto.HashPass
import effects.JwtClock
import effects.TokenExpire
import effects.GenerateUUID
import services.auth.Users
import configuration.types.AppConfig

object Services {
  def make[F[_]: MonadCancelThrow: JwtClock: PasswordHasher[*[_], SCrypt]: GenerateUUID](
      psql: Resource[F, Session[F]],
      appConfig: AppConfig
  ): Services[F] = {
    val tokens = Tokens.create[F](
      TokenExpire.create[F],
      appConfig.jwtIssuer,
      appConfig.jwtExpiration,
      appConfig.jwtKeyConfig.value
    )

    new Services[F](
      auth = Auth.create[F](HashPass.create[F], Users.create[F](psql), tokens),
      demo = Demo.create[F]
    ) {}
  }
}

sealed abstract class Services[F[_]] private (
    val auth: Auth[F],
    val demo: Demo[F]
)
