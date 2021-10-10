package effects

import cats.effect.Sync

import java.time.Clock

trait JwtClock[F[_]] {
  def utcTime: F[Clock]
}

object JwtClock {
  def apply[F[_]: JwtClock]: JwtClock[F] = implicitly

  implicit def JwtClock[F[_]: Sync]: JwtClock[F] =
    new JwtClock[F] {
      def utcTime: F[Clock] =
        Sync[F].delay(Clock.systemUTC())
    }
}
