package services

import dev.profunktor.auth.jwt.JwtToken
import domains.user.{ Email, Name, Password, User, UserId }

trait Auth[F[_]] {
  def login(email: Email, password: Password): F[JwtToken]
  def getUser(token: JwtToken): F[User]
  def logout(token: JwtToken, userId: UserId): F[Unit]
  def register(firstName: Name, lastName: Name, email: Email, password: Password): F[Unit]
}
