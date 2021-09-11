package sql

import skunk._
import skunk.codec.all._

import domains.user._

object codecs {
  val userId: Codec[UserId] = uuid.imap(UserId(_))(_.value)
  val name: Codec[Name] = varchar.imap(Name(_))(_.value)
  val email: Codec[Email] = varchar.imap(Email(_))(_.value)
  val encryptedPassword: Codec[EncryptedPassword] = varchar.imap(EncryptedPassword(_))(_.value)
  val username: Codec[Username] = varchar.imap(Username(_))(_.value)
}
