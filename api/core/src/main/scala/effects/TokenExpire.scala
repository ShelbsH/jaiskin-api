package effects

import cats.implicits._
import cats.effect.kernel.Sync
import pdi.jwt.JwtClaim

import effects.JwtClock
import configuration.types.JwtExpiration

trait TokenExpire[F[_]] {
  def expiration(claim: JwtClaim, exp: JwtExpiration): F[JwtClaim]
}

object TokenExpire {
  def create[F[_]: Sync]: F[TokenExpire[F]] =
    JwtClock[F].utcTime.map { implicit clock =>
      new TokenExpire[F] {
        def expiration(claim: JwtClaim, exp: JwtExpiration): F[JwtClaim] =
          Sync[F].delay(claim.issuedNow.expiresIn(exp.value.toSeconds))
      }
    }
}
