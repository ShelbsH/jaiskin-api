package services

import skunk.Session
import pdi.jwt.JwtAlgorithm
import tsec.passwordhashers._
import tsec.passwordhashers.jca._
import dev.profunktor.auth.jwt.JwtAuth
import cats.effect.{ MonadCancelThrow, Resource }

import tokens.Tokens
import crypto.HashPass
import effects.JwtClock
import services.auth.Users
import effects.TokenExpire
import effects.GenerateUUID
import domains.user.{ JwtUserAuth, User }
import configuration.types.AppConfig
import services.auth.UserAuth

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

    val jwtUserAuth: JwtUserAuth = JwtUserAuth(
      JwtAuth.hmac(
        appConfig.jwtKeyConfig.value.secret.value,
        JwtAlgorithm.HS256
      )
    )

    new Services[F](
      auth = Auth.create[F](HashPass.create[F], Users.create[F](psql), tokens),
      jwtUserAuth = jwtUserAuth,
      userAuth = UserAuth.common[F](psql, jwtUserAuth),
      demo = Demo.create[F]
    ) {}
  }
}

sealed abstract class Services[F[_]] private (
    val auth: Auth[F],
    val jwtUserAuth: JwtUserAuth,
    val userAuth: UserAuth[F, User],
    val demo: Demo[F]
)
