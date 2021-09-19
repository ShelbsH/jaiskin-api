package sql

import skunk._
import skunk.implicits._
import skunk.codec.text._
import natchez.Trace
import fs2.io.net.Network
import cats.implicits._
import cats.effect.std.Console
import cats.effect.Resource
import cats.effect.kernel.{ Concurrent }
import org.typelevel.log4cats.Logger

import configuration.types.PostgresConfig

sealed abstract class PostgreSQL[F[_]](
    val postgresSql: Resource[F, Session[F]]
)

object PostgreSQL {
  def make[F[_]: Concurrent: Network: Trace: Console: Logger](cfg: PostgresConfig): Resource[F, PostgreSQL[F]] = {
    def checkConnection(psql: Resource[F, Session[F]]): F[Unit] =
      psql.use { session =>
        session
          .unique(sql"SELECT version()".query(text))
          .flatMap { text =>
            Logger[F].info(s"postgres is connected at $text")
          }
      }

    Session
      .pooled[F](
        host = cfg.host.value,
        port = cfg.port.value,
        user = cfg.user.value,
        database = cfg.database.value,
        password = Some(cfg.password.value.value),
        max = cfg.max
      )
      .evalTap(checkConnection)
      .map(new PostgreSQL[F](_) {})
  }
}
