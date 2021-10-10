package configuration

import ciris.Secret
import derevo.derive
import derevo.cats.show
import eu.timepit.refined.cats._
import eu.timepit.refined.types.all.UserPortNumber
import eu.timepit.refined.types.string.NonEmptyString
import dev.profunktor.auth.jwt.{ JwtToken }
import scala.concurrent.duration.FiniteDuration
import io.estatico.newtype.macros.newtype

object types {
  case class AppConfig(
      psqlConfig: PostgresConfig,
      jwtKeyConfig: Secret[JwtSecretKeyConfig],
      jwtIssuer: JwtIssuer,
      jwtExpiration: JwtExpiration
  )

  @derive(show)
  @newtype
  case class JwtSecretKeyConfig(secret: NonEmptyString)

  case class JwtUserClaim(iss: String, sub: String)

  @newtype case class JwtIssuer(iss: String)

  @newtype case class JwtUserToken(accessToken: JwtToken)

  @newtype case class JwtExpiration(value: FiniteDuration)

  case class PostgresConfig(
      host: NonEmptyString,
      user: NonEmptyString,
      database: NonEmptyString,
      password: Secret[NonEmptyString],
      port: UserPortNumber,
      max: Int
  )
}
