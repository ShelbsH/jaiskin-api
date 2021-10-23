package services

import cats.implicits._
import cats.effect.kernel.MonadCancelThrow
import dev.profunktor.auth.jwt.JwtToken

import tokens.Tokens
import domains.user._
import crypto.HashPass
import services.auth.Users
import extension.ValidationFields._

trait Auth[F[_]] {
  def login(email: Email, password: Password): F[JwtToken]
  def getUser(token: JwtToken): F[User]
  def logout(token: JwtToken, userId: UserId): F[Unit]
  def register(reg: RegisterCredentials): F[JwtToken]
}

object Auth {
  def create[F[_]: MonadCancelThrow](
      hashPass: HashPass[F],
      users: Users[F],
      tokens: Tokens[F]
  ): Auth[F] =
    new Auth[F] {
      def login(email: Email, password: Password): F[JwtToken] =
        emailInput(email.value) match {
          case Left(error) => ValidationError("Some errors found", error).raiseError[F, JwtToken]
          case Right(email) =>
            users.getUserByEmail(email).flatMap {
              case Some(user) =>
                hashPass
                  .compare(password, user.encryptedPassword)
                  .ensure(EmailOrPasswordInvalid)(_ === true)
                  .flatMap(_ => tokens.make(user.user.id))
              case None => EmailOrPasswordInvalid.raiseError[F, JwtToken]
            }
        }

      def getUser(token: JwtToken): F[User] = ???

      def logout(token: JwtToken, userId: UserId): F[Unit] = ???

      def register(reg: RegisterCredentials): F[JwtToken] =
        (
          firstNameInput(reg.firstName.value),
          lastNameInput(reg.lastName.value),
          usernameInput(reg.username.value),
          emailInput(reg.email.value),
          passwordInput(reg.password.value)
        ).parMapN(Register.apply) match {
          case Left(errors) => ValidationError("Some errors found", errors).raiseError[F, JwtToken]
          case Right(r) =>
            for {
              _ <- users.getUserByEmail(r.email).ensure(EmailInUse(r.email))(_ === none)
              _ <- users.getUserByUsername(r.username).ensure(UsernameInUse(r.username))(_ === none)
              p <- hashPass.encrypt(r.password)
              u <- users.createUser(r, p)
              t <- tokens.make(u.id)
            } yield t
        }
    }
}
