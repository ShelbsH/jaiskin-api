package routes

import cats.{ MonadThrow }
import cats.syntax.flatMap._
import cats.syntax.applicativeError._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import services.Demo
import domains.demo.ParamIsEmpty

final case class DemoRoute[F[_]: MonadThrow](demo: Demo[F]) extends Http4sDsl[F] {
  val route: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "demo" / greet =>
        demo
          .message(greet)
          .flatMap(Ok(_))
          .recoverWith { case ParamIsEmpty(msg) =>
            BadRequest(msg)
          }
    }
}
