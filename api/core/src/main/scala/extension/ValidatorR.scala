package extension

import cats.data.EitherNel
import cats.syntax.either._

object ValidatorR {
  implicit class RefinedValidation[FTP, E](str: Either[String, FTP]) {
    def errorMsgNel(e: E): EitherNel[E, FTP] =
      str match {
        case Left(_)  => str.leftMap(_ => e).toEitherNel
        case Right(v) => v.asRight
      }
  }
}
