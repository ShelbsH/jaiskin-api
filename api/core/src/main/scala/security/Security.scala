package security

import skunk.Session
import cats.implicits._
import pdi.jwt.JwtAlgorithm
import cats.effect.Resource
import cats.effect.kernel.Sync
import tsec.passwordhashers._
import tsec.passwordhashers.jca._
import dev.profunktor.auth.jwt.JwtAuth

import tokens.Tokens
import services.Auth
import crypto.HashPass
import configuration.types.AppConfig
import services.auth.{ Users, UserAuth }
import domains.user.{ JwtUserAuth, User }
import effects.{ TokenExpire, GenerateUUID }

object Security {
  def make[F[_]: Sync: PasswordHasher[*[_], SCrypt]: GenerateUUID](
      psql: Resource[F, Session[F]],
      appConfig: AppConfig
  ): F[Security[F]] = {
    val jwtUserAuth: JwtUserAuth = JwtUserAuth(
      JwtAuth.hmac(
        appConfig.jwtKeyConfig.value.secret.value,
        JwtAlgorithm.HS256
      )
    )

    for {
      exp <- TokenExpire.create[F]
      tokens    = Tokens.create[F](
        exp,
        appConfig.jwtIssuer,
        appConfig.jwtExpiration,
        appConfig.jwtKeyConfig.value
      )
      hashPass  = HashPass.create[F]
      users     = Users.create[F](psql)
      usersAuth = UserAuth.common(psql, jwtUserAuth)
      auth      = Auth.create[F](hashPass, users, tokens)
    } yield new Security[F](auth, usersAuth, jwtUserAuth) {}
  }
}

sealed abstract class Security[F[_]] private (
    val auth: Auth[F],
    val userAuth: UserAuth[F, User],
    val jwtUserAuth: JwtUserAuth
)
