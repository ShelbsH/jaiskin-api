package effects

import cats.ApplicativeThrow
import cats.effect.Sync

import java.util.UUID

trait GenerateUUID[F[_]] {
  def make: F[UUID]
  def read(string: String): F[UUID]
}

object GenerateUUID {
  def apply[F[_]: GenerateUUID]: GenerateUUID[F] = implicitly

  implicit def create[F[_]: Sync]: GenerateUUID[F] =
    new GenerateUUID[F] {
      def make: F[UUID] = Sync[F].delay(UUID.randomUUID)

      def read(uuidString: String): F[UUID] =
        ApplicativeThrow[F].catchNonFatal(UUID.fromString(uuidString))
    }
}
