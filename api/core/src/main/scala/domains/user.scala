package domains

import java.util.UUID
import scala.util.control.NoStackTrace
import derevo.derive
import derevo.circe.magnolia.{ encoder, decoder }
import derevo.cats.{ eqv, show }
import eu.timepit.refined.auto._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.refined._
import io.estatico.newtype.macros.newtype

object user {

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class UserId(value: UUID)

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class UserName(value: String)

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class Email(value: String)

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class Password(value: String)

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class Name(value: String)

  @derive(encoder, decoder, eqv, show)
  case class User(id: UserId, firstName: Name, lastName: Name, username: UserName, email: Email)

  /*
   * -------------------------------------
   * Register
   * -------------------------------------
   */
  @derive(encoder, decoder)
  @newtype case class NameParam(value: NonEmptyString) {
    def toValue: Name = Name(value.toLowerCase)
  }

  type EmailRegex = String Refined MatchesRegex["""@\"^([\\w\\.\\-]+)@([\\w\\-]+)((\\.(\\w){2,3})+)$\"""]

  @derive(encoder, decoder)
  @newtype case class EmailParam(value: EmailRegex) {
    def toValue: Email = Email(value.toLowerCase)
  }

  @derive(encoder, decoder)
  @newtype case class PasswordParam(value: NonEmptyString) {
    def toValue: Password = Password(value.toLowerCase)
  }

  @derive(encoder, decoder)
  case class Register(firstName: NameParam, lastName: NameParam, email: EmailParam, password: PasswordParam)

  /*
   * -------------------------------------
   * Register Domain Errors
   * -------------------------------------
   */
  case class EmailInUse(email: Email) extends NoStackTrace
  case class UserNameInUse(username: UserName) extends NoStackTrace
  case class InvalidEmail(msg: String) extends NoStackTrace //This will be moved into a file

  /*
   * -------------------------------------
   * Login
   * -------------------------------------
   */
  @derive(encoder, decoder)
  case class Login(email: EmailParam, password: PasswordParam)

  /*
   * -------------------------------------
   * Login Domain Errors
   * -------------------------------------
   */
  case class EmailOrPasswordInvalid(msg: String) extends NoStackTrace
}
