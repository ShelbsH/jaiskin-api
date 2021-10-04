package routes.auth

import cats.MonadThrow
import cats.syntax.flatMap._
import cats.syntax.applicativeError._
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.JsonDecoder
import org.http4s.circe.CirceEntityCodec._

import services.Auth
import domains.user._

case class RegisterRoute[F[_]: MonadThrow: JsonDecoder](auth: Auth[F]) extends Http4sDsl[F] {
  private[routes] val pathPrefix = "/auth"

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] { 
      case req @ POST -> Root / "register" =>
        req
          .asJsonDecode[RegisterCredentials]
          .flatMap { credentials =>
            auth
              .register(credentials)
              .flatMap(Created(_))
              .recoverWith {
                case validationError: ValidationError => UnprocessableEntity(validationError)
                case EmailInUse(email)                => Conflict(ErrorMessage(s"Email, $email already exists on this account"))
                case UsernameInUse(username)          => Conflict(ErrorMessage(s"Username, $username already exists on this account"))
              }
          }
    }

  val route: HttpRoutes[F] = Router(
    pathPrefix -> httpRoutes
  )
}
