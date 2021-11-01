package routes.auth

import cats.{ Defer, Monad }
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._
import org.http4s.{ AuthedRoutes, HttpRoutes }
import org.http4s.server.{ AuthMiddleware, Router }
import io.circe.generic.auto._

import domains.user.{ User }

case class GetUserRoute[F[_]: Monad: Defer]() extends Http4sDsl[F] {
  private[routes] val pathPrefix = "/auth"

  private val authRoute: AuthedRoutes[User, F] =
    AuthedRoutes.of[User, F] {
      case GET -> Root / "user" as user => Ok(user)
    }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] =
    Router(
      pathPrefix -> authMiddleware(authRoute)
    )
}
