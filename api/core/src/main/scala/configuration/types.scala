package configuration

import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.all.UserPortNumber
import ciris.Secret

object types {
  case class AppConfig(
      psqlConfig: PostgresConfig
  )

  case class PostgresConfig(
      host: NonEmptyString,
      user: NonEmptyString,
      database: NonEmptyString,
      password: Secret[NonEmptyString],
      port: UserPortNumber,
      max: Int
  )
}
