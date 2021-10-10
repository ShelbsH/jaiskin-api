package http

import cats.implicits._
import cats.effect.Async
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.middleware.CORS
import org.http4s.server.Router
import org.http4s.server.middleware.{ RequestLogger, ResponseLogger }

import routes.version
import services.Services
import routes.DemoRoute
import routes.auth.RegisterRoute
import routes.auth.LoginRoute

object HttpAPI {
  def create[F[_]: Async](services: Services[F]): HttpAPI[F] =
    new HttpAPI[F](services) {}
}

sealed abstract class HttpAPI[F[_]: Async] private (val services: Services[F]) {
  private val demoRoute: HttpRoutes[F]     = DemoRoute[F](services.demo).route
  private val registerRoute: HttpRoutes[F] = RegisterRoute[F](services.auth).route
  private val loginRoute: HttpRoutes[F]    = LoginRoute[F](services.auth).route
  private val composeRoutes: HttpRoutes[F] = demoRoute <+> registerRoute <+> loginRoute

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
