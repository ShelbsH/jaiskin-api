package domains

import java.util.UUID
import cats.data.NonEmptyList
import scala.util.control.NoStackTrace
import derevo.derive
import derevo.cats.{ eqv, show }
import derevo.circe.magnolia.{ decoder, encoder }
import eu.timepit.refined.auto._
import eu.timepit.refined.api.{ Refined, RefinedTypeOps }
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.refined._
import io.estatico.newtype.macros.newtype

import optics.uuid

object user {
  @derive(encoder, decoder, eqv, show, uuid)
  @newtype
  case class UserId(value: UUID)

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class Username(value: String)

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class Email(value: String)

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class Password(value: String)

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class EncryptedPassword(value: String)

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class Name(value: String)

  @derive(encoder, decoder, eqv, show)
  case class User(id: UserId, firstName: Name, lastName: Name, username: Username, email: Email)

  /*
   * -------------------------------------
   * Register
   * -------------------------------------
   */
  type InputR = NonEmptyString
  object InputR extends RefinedTypeOps[InputR, String]

  type NameR = String Refined MatchesRegex["""^[^\d\W]+$"""]
  object NameR extends RefinedTypeOps[NameR, String]

  type UsernameR = NonEmptyString
  object UsernameR extends RefinedTypeOps[NameR, String]

  type EmailR = String Refined MatchesRegex["""^[^\s@]+@[^\s@]+\.[^\s@]{2,}$"""]
  object EmailR extends RefinedTypeOps[EmailR, String]

  type PasswordR = NonEmptyString
  object PasswordR extends RefinedTypeOps[PasswordR, String]

  @derive(encoder, decoder)
  @newtype case class NameParam(value: NonEmptyString) {
    def toValue: Name = Name(value.toLowerCase)
  }

  @derive(encoder, decoder)
  case class Register(
      firstName: Name,
      lastName: Name,
      username: Username,
      email: Email,
      password: Password
  )

  case class RegisterCredentials(
      firstName: Name,
      lastName: Name,
      username: Username,
      email: Email,
      password: Password
  )

  /*
   * -------------------------------------
   * Register Domain Errors
   * -------------------------------------
   */
  case class ValidationError(message: String, errors: NonEmptyList[String]) extends NoStackTrace
  case class EmailInUse(email: Email) extends NoStackTrace
  case class UsernameInUse(username: Username) extends NoStackTrace

  /*
   * -------------------------------------
   * Login
   * -------------------------------------
   */
  @derive(encoder, decoder)
  case class Login(email: Email, password: Password)

  /*
   * -------------------------------------
   * Login Domain Errors
   * -------------------------------------
   */
  case class EmailOrPasswordInvalid(message: String) extends NoStackTrace
}
