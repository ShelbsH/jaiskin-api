package extension

import cats.data.NonEmptyList

import domains.user._
import extension.ValidatorR._

object ValidationFields {
  def firstNameInput(name: String): Either[NonEmptyList[String], Name] =
    InputR
      .from(name)
      .errorMsgNel("First name can't be empty")
      .flatMap(input => NameR.from(input.value).errorMsgNel("Enter a valid first name"))
      .map(n => Name(n.value))

  def lastNameInput(name: String): Either[NonEmptyList[String], Name] =
    InputR
      .from(name)
      .errorMsgNel("Last name can't be empty")
      .flatMap(input => NameR.from(input.value).errorMsgNel("Enter a valid last name"))
      .map(n => Name(n.value))
  
  //TODO: Add some validation rules for the username
  def usernameInput(username: String): Either[NonEmptyList[String], Username] =
    UsernameR
      .from(username)
      .errorMsgNel("Username can't be empty")
      .map(u => Username(u.value))

  def emailInput(email: String): Either[NonEmptyList[String], Email] =
    InputR
      .from(email)
      .errorMsgNel("Email can't be empty")
      .flatMap(email => EmailR.from(email.value).errorMsgNel("Enter a valid email address"))
      .map(e => Email(e.value))

  //TODO: Add some validation rules for the password
  def passwordInput(password: String): Either[NonEmptyList[String], Password] =
    PasswordR
      .from(password)
      .errorMsgNel("Password can't be empty")
      .map(p => Password(p.value))
}
