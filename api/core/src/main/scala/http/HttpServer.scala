package http

import cats.implicits._
import cats.effect.{Async, Resource}
import org.http4s.HttpApp
import org.http4s.server.Server
import org.http4s.blaze.server.BlazeServerBuilder

trait HttpServer[F[_]] {
  def addBlazeServer(httpApp: HttpApp[F], port: Int): F[Resource[F, Server]]
}

object HttpServer {
  def apply[F[_]: HttpServer]: HttpServer[F] = implicitly

  implicit def forAsync[F[_]](implicit A: Async[F]): HttpServer[F] =
    new HttpServer[F] {
      def addBlazeServer(httpApp: HttpApp[F], port: Int): F[Resource[F, Server]] =
        A.executionContext
          .map {
            BlazeServerBuilder[F](_)
              .withHttpApp(httpApp)
              .bindHttp(port, "localhost")
              .resource
          }
    }
}
