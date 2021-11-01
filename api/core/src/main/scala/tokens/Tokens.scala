package tokens

import cats.MonadThrow
import cats.implicits._
import io.circe.syntax._
import io.circe.generic.auto._
import pdi.jwt.JwtClaim
import pdi.jwt.JwtAlgorithm
import dev.profunktor.auth.jwt._

import effects.TokenExpire
import domains.user.UserId
import configuration.types._

trait Tokens[F[_]] {
  def make(id: UserId): F[JwtToken]
}

object Tokens {
  def create[F[_]: MonadThrow](
      tokenExpire: TokenExpire[F],
      jwtIssuer: JwtIssuer,
      jwtExpiration: JwtExpiration,
      jwtSecret: JwtSecretKeyConfig
  ): Tokens[F] = {
    new Tokens[F] {
      def make(id: UserId): F[JwtToken] =
        tokenExpire
          .expiration(
            JwtClaim(
              JwtUserClaim(
                jwtIssuer.iss,
                id.value.toString
              ).asJson.noSpaces
            ),
            jwtExpiration
          )
          .flatMap { claim =>
            jwtEncode[F](claim, JwtSecretKey(jwtSecret.secret.value), JwtAlgorithm.HS256)
          }
    }
  }
}
