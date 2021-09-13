package services

import skunk.Session
import cats.effect.{ MonadCancelThrow, Resource }
import tsec.passwordhashers._
import tsec.passwordhashers.jca._

import crypto.HashPass
import services.auth.Users
import effects.GenerateUUID

object Services {
  def make[F[_]: MonadCancelThrow: PasswordHasher[*[_], SCrypt]: GenerateUUID](
      psql: Resource[F, Session[F]]
  ): Services[F] =
    new Services[F](
      auth = Auth.create[F](psql, HashPass.create[F], Users.create[F](psql)),
      demo = Demo.create[F]
    ) {}
}

sealed abstract class Services[F[_]] private (
    val auth: Auth[F],
    val demo: Demo[F]
)
