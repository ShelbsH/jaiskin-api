import cats.effect.{ ExitCode, IO, IOApp }
import org.http4s.implicits._

import http.HttpServer
import services.Demo
import routes.DemoRoute

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    HttpServer[IO]
      .addBlazeServer(DemoRoute(Demo.create[IO]).route.orNotFound, 8000) //TODO: Create an HttApi file to compose
      .flatMap(_.use(_ => IO.never))
}
