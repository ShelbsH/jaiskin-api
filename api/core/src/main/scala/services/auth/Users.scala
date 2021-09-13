package services.auth

import skunk._
import skunk.implicits._
import cats.implicits._
import cats.effect.Resource
import cats.effect.kernel.MonadCancelThrow
import dev.profunktor.auth.jwt.JwtToken

import domains.ID
import sql.codecs._
import domains.user._
import effects.GenerateUUID

trait Users[F[_]] {
  def createUser(reg: Register, password: EncryptedPassword): F[User]
  def getUserByEmail(email: Email): F[Option[User]]
  def getUserByToken(token: JwtToken): F[Option[User]]
  def getUserByUsername(username: Username): F[Option[User]]
}

object Users {
  import AuthSQL._

  def create[F[_]: MonadCancelThrow: GenerateUUID](psql: Resource[F, Session[F]]): Users[F] =
    new Users[F] {
      def createUser(reg: Register, password: EncryptedPassword): F[User] =
        ID.make[F, UserId].flatMap { id =>
          val user = User(id, reg.firstName, reg.lastName, reg.username, reg.email)

          psql
            .flatMap(_.prepare(insertUser))
            .use { cmd =>
              cmd.execute(user ~ password).as(user)
            }
        }

      def getUserByEmail(email: Email): F[Option[User]] =
        psql
          .flatMap(_.prepare(selectUserByEmail))
          .use { q =>
            q.option(email)
          }

      def getUserByToken(token: JwtToken): F[Option[User]] = ???

      def getUserByUsername(username: Username): F[Option[User]] =
        psql
          .flatMap(_.prepare(selectUserByUsername))
          .use { q =>
            q.option(username)
          }
    }
}

private object AuthSQL {
  val encode: Encoder[User ~ EncryptedPassword] =
    (userId ~ name ~ name ~ username ~ email ~ encryptedPassword)
      .contramap {
        case u ~ p =>
          u.id ~ u.firstName ~ u.lastName ~ u.username ~ u.email ~ p
      }

  val insertUser: Command[User ~ EncryptedPassword] =
    sql"""
         INSERT INTO users
         VALUES ($encode)
       """.command

  val selectUserByUsername: Query[Username, User] =
    sql"""
         SELECT uuid, first_name, last_name, username, email 
         FROM users
         WHERE username = $username
       """
      .query(userId ~ name ~ name ~ username ~ email)
      .gmap[User]

  val selectUserByEmail: Query[Email, User] =
    sql"""
         SELECT uuid, first_name, last_name, username, email
         FROM users
         WHERE email = $email
       """
      .query(userId ~ name ~ name ~ username ~ email)
      .gmap[User]
}