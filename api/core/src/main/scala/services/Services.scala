package services

import cats.ApplicativeThrow

object Services {
  def make[F[_]: ApplicativeThrow]: Services[F] =
    new Services[F](demo = Demo.create[F]) {}
}

sealed abstract class Services[F[_]] private (
    val demo: Demo[F]
)
