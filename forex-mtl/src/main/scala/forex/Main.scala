package forex

import scala.concurrent.ExecutionContext

import cats.effect._
import forex.config._
import fs2.Stream
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.client.blaze._
import org.http4s.client._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder[IO](executionContext).resource.use { httpClient =>
      new Application[IO].stream(executionContext, httpClient).compile.drain.as(ExitCode.Success)
    }
  }
}

class Application[F[_]: ConcurrentEffect: Timer] {

  def stream(ec: ExecutionContext, client: Client[F]): Stream[F, Unit] =
    for {
      config <- Config.stream("app")
      module = new Module[F](config, client)
      _ <- BlazeServerBuilder[F](ec)
            .bindHttp(config.http.port, config.http.host)
            .withHttpApp(module.httpApp)
            .serve
    } yield ()
}
