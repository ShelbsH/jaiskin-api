package http

import cats.implicits._
import cats.effect.Async
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.{ RequestLogger, ResponseLogger }
import dev.profunktor.auth.JwtAuthMiddleware

import routes.auth._
import routes.version
import routes.DemoRoute
import services.Services
import security.Security
import domains.user.User

object HttpAPI {
  def create[F[_]: Async](services: Services[F], security: Security[F]): HttpAPI[F] =
    new HttpAPI[F](services, security) {}
}

sealed abstract class HttpAPI[F[_]: Async] private (val services: Services[F], security: Security[F]) {
  private val userMiddleware = JwtAuthMiddleware[F, User](security.jwtUserAuth.value, security.userAuth.findUser)

  //Public Routes
  private val demoRoute: HttpRoutes[F]     = DemoRoute[F](services.demo).route
  private val registerRoute: HttpRoutes[F] = RegisterRoute[F](security.auth).route
  private val loginRoute: HttpRoutes[F]    = LoginRoute[F](security.auth).route

  //AuthRoutes
  private val getUserRoute: HttpRoutes[F]     = GetUserRoute[F]().routes(userMiddleware)
  private val getJwtTokenRoute: HttpRoutes[F] = GetJwtTokenRoute[F](security.auth).routes(userMiddleware)

  private val composeRoutes: HttpRoutes[F] =
    demoRoute <+> registerRoute <+> loginRoute <+> getUserRoute <+> getJwtTokenRoute

  private val middleware: HttpRoutes[F] => HttpRoutes[F] =
    http => CORS(http)

  private val requestLogger: HttpApp[F] => HttpApp[F] =
    (http: HttpApp[F]) => RequestLogger.httpApp(true, true)(http)

  private val responseLogger: HttpApp[F] => HttpApp[F] =
    (http: HttpApp[F]) => ResponseLogger.httpApp(true, true)(http)

  private val loggerMiddleware: HttpApp[F] => HttpApp[F] =
    (http => requestLogger(http))
      .andThen(responseLogger(_))

  private val router: HttpRoutes[F] = Router(version.v1 -> composeRoutes)

  val routes: HttpApp[F] = loggerMiddleware(middleware(router).orNotFound)
}
