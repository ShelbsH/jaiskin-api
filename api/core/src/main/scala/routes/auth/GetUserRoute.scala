package routes.auth

import cats.{ Defer, Monad }
import cats.syntax.flatMap._
import cats.syntax.traverse._
import dev.profunktor.auth.AuthHeaders
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._
import org.http4s.{ AuthedRoutes, HttpRoutes }
import org.http4s.server.{ AuthMiddleware, Router }
import io.circe.generic.auto._

import services.Auth
import domains.user.User

case class GetUserRoute[F[_]: Monad: Defer](auth: Auth[F]) extends Http4sDsl[F] {
  private[routes] val pathPrefix = "/auth"

  private val authRoute: AuthedRoutes[User, F] =
    AuthedRoutes.of[User, F] {
      case ar @ GET -> Root / "get_user" as _ =>
        AuthHeaders
          .getBearerToken(ar.req)
          .traverse(auth.getUser(_))
          .flatMap(Ok(_))
    }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] =
    Router(
      pathPrefix -> authMiddleware(authRoute)
    )
}
