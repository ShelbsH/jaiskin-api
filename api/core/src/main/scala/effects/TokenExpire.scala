package effects

import cats.Functor
import cats.implicits._
import pdi.jwt.JwtClaim

import effects.JwtClock
import configuration.types.JwtExpiration

trait TokenExpire[F[_]] {
  def expiration(claim: JwtClaim, exp: JwtExpiration): F[JwtClaim]
}

object TokenExpire {
  def create[F[_]: JwtClock: Functor]: TokenExpire[F] =
    new TokenExpire[F] {
      def expiration(claim: JwtClaim, exp: JwtExpiration): F[JwtClaim] =
        JwtClock[F].utcTime.map { implicit clock =>
          claim.expiresIn(exp.value.toMillis)
        }
    }
}
