package services

import cats.MonadThrow
import cats.syntax.either._
import eu.timepit.refined.types.string.NonEmptyString

import domains.demo.ParamIsEmpty

trait Demo[F[_]] {
  def message(m: String): F[String]
}

object Demo {
  def create[F[_]: MonadThrow]: Demo[F] =
    new Demo[F] {
      override def message(m: String): F[String] =
        NonEmptyString
          .from(m)
          .map(_.toString)
          .leftMap(_ => ParamIsEmpty("The value is empty"))
          .liftTo[F]
    }
}
