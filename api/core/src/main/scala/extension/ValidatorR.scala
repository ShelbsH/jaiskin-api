package extension

import cats.data.EitherNel
import cats.syntax.either._

object ValidatorR {
  implicit class RefinedValidation[FTP, E](value: Either[String, FTP]) {
    def errorMsgNel(e: E): EitherNel[E, FTP] =
      value.leftMap(_ => e).toEitherNel
  }
}
