package http

import cats.MonadThrow
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.server.middleware.CORS
import org.http4s.server.Router
import org.http4s.circe.JsonDecoder

import routes.version
import services.Services
import routes.DemoRoute
import routes.auth.RegisterRoute

object HttpAPI {
  def create[F[_]: MonadThrow: JsonDecoder](services: Services[F]): HttpAPI[F] =
    new HttpAPI[F](services) {}
}

sealed abstract class HttpAPI[F[_]: MonadThrow: JsonDecoder] private (val services: Services[F]) {
  private val demoRoute: HttpRoutes[F] = DemoRoute[F](services.demo).route
  private val registerRoute: HttpRoutes[F] = RegisterRoute[F](services.auth).route

  private val composeRoutes: HttpRoutes[F] = demoRoute <+> registerRoute

  private val middleware: HttpRoutes[F] => HttpRoutes[F] =
    http => CORS(http)

  val routes: HttpRoutes[F] = middleware(
    Router(
      version.v1 -> composeRoutes
    )
  )
}
