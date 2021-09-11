package services

import skunk.Session
import cats.implicits._
import cats.effect.Resource
import cats.effect.kernel.MonadCancelThrow
import dev.profunktor.auth.jwt.JwtToken

import domains.user._
import crypto.HashPass
import services.auth.Users
import extension.ValidationFields._

trait Auth[F[_]] {
  def login(email: Email, password: Password): F[JwtToken]
  def getUser(token: JwtToken): F[User]
  def logout(token: JwtToken, userId: UserId): F[Unit]
  def register(reg: RegisterCredentials): F[User]
}

object Auth {
  def create[F[_]: MonadCancelThrow](
      psql: Resource[F, Session[F]],
      hashPass: HashPass[F],
      users: Users[F]
  ): Auth[F] =
    new Auth[F] {
      def login(email: Email, password: Password): F[JwtToken] = ???

      def getUser(token: JwtToken): F[User] = ???

      def logout(token: JwtToken, userId: UserId): F[Unit] = ???

      def register(reg: RegisterCredentials): F[User] =
        (
          firstNameInput(reg.firstName.value),
          lastNameInput(reg.lastName.value),
          usernameInput(reg.username.value),
          emailInput(reg.email.value),
          passwordInput(reg.password.value)
        ).parMapN(Register.apply) match {
          case Left(errors) => ValidationError("some errors found", errors).raiseError[F, User]
          case Right(r) =>
            for {
              _ <- users.getUserByEmail(r.email).ensure(EmailInUse(r.email))(_ === none)
              _ <- users.getUserByUsername(r.username).ensure(UsernameInUse(r.username))(_ === none)
              p <- hashPass.encrypt(r.password)
              u <- users.createUser(r, p)
            } yield u
        }
    }
}
