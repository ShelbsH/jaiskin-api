package routes.auth

import cats.implicits._
import cats.{ Defer, Monad }
import org.http4s.AuthedRoutes
import org.http4s.server.Router
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._
import io.circe.generic.auto._

import services.Auth
import domains.user.User
import org.http4s.server.AuthMiddleware
import org.http4s.HttpRoutes

case class GetJwtTokenRoute[F[_]: Monad: Defer](auth: Auth[F]) extends Http4sDsl[F] {
  private[routes] val pathPrefix = "/auth"

  private val authRoute: AuthedRoutes[User, F] =
    AuthedRoutes.of[User, F] { 
      case GET -> Root / "token" as user =>
        auth
          .token(user.id)
          .flatMap(Ok(_))
    }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] =
    Router(
      pathPrefix -> authMiddleware(authRoute)
    )
}
