import scala.concurrent.ExecutionContext.Implicits.global
import cats.MonadThrow
import cats.syntax.flatMap._
import cats.syntax.applicativeError._
import cats.effect.{ Sync, ExitCode, IO, IOApp }
import org.http4s.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.blaze.server.BlazeServerBuilder

//Domains
sealed trait MessageError extends Throwable
case class ParamIsEmpty(msg: String) extends MessageError

trait Demo[F[_]] {
  def message(m: String): F[String]
}

object DemoUse {
  def create[F[_]](implicit F: Sync[F]): F[Demo[F]] =
    F.delay {
      new Demo[F] {
        override def message(m: String): F[String] =
          if (m.isEmpty) F.raiseError(ParamIsEmpty(m))
          else F.pure(m)
      }
    }
}

final case class DemoRoute[F[_]: MonadThrow](demo: Demo[F]) extends Http4sDsl[F] {
  val route: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "demo" / greet =>
      demo
        .message(greet)
        .flatMap(Ok(_))
        .recoverWith {
          case ParamIsEmpty(msg) => BadRequest(msg)
        }
    }
}

object DemoApp extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    DemoUse
      .create[IO]
      .flatMap { demo =>
        BlazeServerBuilder[IO](global)
          .bindHttp(8000, "localhost")
          .withHttpApp(DemoRoute[IO](demo).route.orNotFound)
          .serve
          .compile
          .drain
          .as(ExitCode.Success)
      }
}
