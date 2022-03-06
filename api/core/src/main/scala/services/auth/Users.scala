package services.auth

import skunk._
import skunk.implicits._
import io.circe.Decoder
import io.circe.parser.{ decode => jsonDecode }
import cats.implicits._
import cats.effect.Resource
import cats.effect.kernel.MonadCancelThrow
import pdi.jwt.JwtClaim
import dev.profunktor.auth.jwt._

import domains.ID
import sql.codecs._
import domains.user._
import effects.GenerateUUID

trait Users[F[_]] {
  def createUser(reg: Register, password: EncryptedPassword): F[User]
  def getUserByEmail(email: Email): F[Option[UserWithPassword]]
  def getUserByUsername(username: Username): F[Option[User]]
  def getUserById(userId: UserId): F[User]
}

trait UserAuth[F[_], A] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[A]]
}

object UserAuth {
  import AuthSQL._
  
  /**
    * Note: The subject's field value from JwtClaim isn't present 
    * after decoding the token with jwtDecode from http4s-jwt-auth 
    * (only works in v1.0.0). Until it's resolved, use json decode 
    * from circe parser for now.
    */

  case class JwtSubject(sub: String)

  implicit val jwtSubjectDecode: Decoder[JwtSubject] =
    Decoder.forProduct1("sub")(JwtSubject.apply)

  def common[F[_]: MonadCancelThrow: GenerateUUID](
      psql: Resource[F, Session[F]],
      jwtUserAuth: JwtUserAuth
  ): UserAuth[F, User] =
    new UserAuth[F, User] {
      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[User]] =
        jwtDecode[F](token, jwtUserAuth.value).flatMap { claim =>
          jsonDecode(claim.content).toOption.traverse { subject =>
            ID.read[F, UserId](subject.sub).flatMap { id =>
              psql
                .flatMap(_.prepare(selectUserById))
                .use { q =>
                  q.unique(id)
                }
            }
          }
        }
    }
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

      def getUserByEmail(email: Email): F[Option[UserWithPassword]] =
        psql
          .flatMap(_.prepare(selectUserByEmail))
          .use { q =>
            q.option(email)
              .map {
                case Some(u ~ p) => UserWithPassword(u, p).some
                case None        => none[UserWithPassword]
              }
          }

      def getUserByUsername(username: Username): F[Option[User]] =
        psql
          .flatMap(_.prepare(selectUserByUsername))
          .use { q =>
            q.option(username)
          }

      def getUserById(userId: UserId): F[User] =
        psql
          .flatMap(_.prepare(selectUserById))
          .use { q =>
            q.unique(userId)
          }
    }
}

private object AuthSQL {
  val codec: Codec[User ~ EncryptedPassword] =
    (userId ~ name ~ name ~ username ~ email ~ encryptedPassword)
      .imap {
        case (id ~ f ~ l ~ u ~ e ~ p) => 
          User(id, f, l, u, e) ~ EncryptedPassword(p.value)
      } {
        case u ~ p => 
          u.id ~ u.firstName ~ u.lastName ~ u.username ~ u.email ~ p
      }

  val insertUser: Command[User ~ EncryptedPassword] =
    sql"""
         INSERT INTO users
         VALUES ($codec)
       """.command

  val selectUserByUsername: Query[Username, User] =
    sql"""
         SELECT uuid, first_name, last_name, username, email 
         FROM users
         WHERE username = $username
       """
      .query(userId ~ name ~ name ~ username ~ email)
      .gmap[User]

  val selectUserByEmail: Query[Email, User ~ EncryptedPassword] =
    sql"""
         SELECT * FROM users
         WHERE email = $email
       """
      .query(codec)
      .gmap[User ~ EncryptedPassword]

  val selectUserById: Query[UserId, User] =
    sql"""
         SELECT uuid, first_name, last_name, username, email 
         FROM users
         WHERE uuid = $userId
      """
      .query(userId ~ name ~ name ~ username ~ email)
      .gmap[User]
}
