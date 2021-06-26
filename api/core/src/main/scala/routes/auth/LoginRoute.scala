package routes.auth

import cats.MonadThrow
import cats.syntax.flatMap._
import cats.syntax.applicativeError._
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import org.http4s.circe.JsonDecoder
import org.http4s.circe.CirceEntityCodec._
import io.circe.generic.auto._

import services.Auth
import domains.user.{ EmailOrPasswordInvalid, Login }

final case class LoginRoute[F[_]: MonadThrow: JsonDecoder](auth: Auth[F]) extends Http4sDsl[F] {
  private[routes] val pathPrefix = "/auth"

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "login" =>
        req.asJsonDecode[Login].flatMap { user =>
          auth
            .login(user.email.toValue, user.password.toValue)
            .flatMap(Ok(_))
            .recoverWith {
              case EmailOrPasswordInvalid(msg) =>  Forbidden(msg)
            }
        }
    }

  val route: HttpRoutes[F] = Router(
    pathPrefix -> httpRoutes
  )
}
