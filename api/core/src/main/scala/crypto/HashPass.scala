package crypto

import cats.Functor
import cats.implicits._
import tsec.passwordhashers._
import tsec.passwordhashers.jca._

import domains.user.{ Password, EncryptedPassword }

trait HashPass[F[_]] {
  def encrypt(password: Password): F[EncryptedPassword]
}

object HashPass {
  def create[F[_]: PasswordHasher[*[_], SCrypt]: Functor]: HashPass[F] =
    new HashPass[F] {
      def encrypt(password: Password): F[EncryptedPassword] =
        SCrypt
          .hashpw[F](password.value.toCharArray)
          .map(h => EncryptedPassword(h.mkString))
    }
}
