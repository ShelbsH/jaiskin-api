package optics

import java.util.UUID
import monocle.Iso

import extension.derevo.Derive

trait IsUUID[A] {
  def _UUID: Iso[UUID, A]
}

object IsUUID {
  def apply[A: IsUUID]: IsUUID[A] = implicitly

  implicit def identityUUID: IsUUID[UUID] =
    new IsUUID[UUID] {
      val _UUID = Iso[UUID, UUID](identity(_))(identity(_))
    }
}

object uuid extends Derive[IsUUID]