package routes.auth

import cats.implicits._
import cats.{ Defer, Monad }
import dev.profunktor.auth.AuthHeaders
import org.http4s.dsl.Http4sDsl
import org.http4s.{ AuthedRoutes, HttpRoutes }
import org.http4s.server.{ AuthMiddleware, Router }

import services.Auth
import domains.user.User

case class LogoutRoute[F[_]: Defer: Monad](auth: Auth[F]) extends Http4sDsl[F] {
  private[routes] val pathPrefix = "/auth"

  private val authRoute: AuthedRoutes[User, F] =
    AuthedRoutes.of[User, F] {
      case ar @ POST -> Root / "logout" as user =>
        AuthHeaders
          .getBearerToken(ar.req)
          .traverse_(auth.logout(_, user.id)) *> NoContent()
    }

  def route(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] =
    Router(
      pathPrefix -> authMiddleware(authRoute)
    )
}
