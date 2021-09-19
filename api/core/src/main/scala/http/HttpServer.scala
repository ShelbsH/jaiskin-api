package http

import cats.implicits._
import cats.effect.{ Async, Resource }
import org.http4s.HttpApp
import org.http4s.server.Server
import org.http4s.blaze.server.BlazeServerBuilder
import org.typelevel.log4cats.Logger

trait HttpServer[F[_]] {
  def addBlazeServer(httpApp: HttpApp[F], port: Int): F[Resource[F, Server]]
}

object HttpServer {
  def apply[F[_]: HttpServer]: HttpServer[F] = implicitly

  private def httpServerLog[F[_]: Logger](server: Server): F[Unit] =
    Logger[F].info(s"Http server is connected to address ${server.address}")

  implicit def forAsync[F[_]: Logger](implicit A: Async[F]): HttpServer[F] =
    new HttpServer[F] {
      def addBlazeServer(httpApp: HttpApp[F], port: Int): F[Resource[F, Server]] =
        A.executionContext
          .map {
            BlazeServerBuilder[F](_)
              .withHttpApp(httpApp)
              .bindHttp(port, "localhost")
              .resource
              .evalTap(httpServerLog(_))
          }
    }
}
