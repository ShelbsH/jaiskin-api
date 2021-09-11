package domains

import cats.Functor
import cats.syntax.functor._

import optics.IsUUID
import effects.GenerateUUID

object ID {
  def make[F[_]: Functor: GenerateUUID, A: IsUUID]: F[A] =
    GenerateUUID[F].make.map(IsUUID[A]._UUID.get)

  def read[F[_]: Functor: GenerateUUID, A: IsUUID](str: String): F[A] =
    GenerateUUID[F].read(str).map(IsUUID[A]._UUID.get)
}
