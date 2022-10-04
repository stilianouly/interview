package forex.services.rates

import cats.Applicative
import cats.effect.Sync
import forex.config.ApplicationConfig
import forex.repository.oneframe.GetOneFrameDataRepository
import interpreters._
import org.http4s.client.Client

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = new OneFrameDummy[F]()

  def oneFrameRateInterpreter[F[_]: Sync](client: Client[F], config: ApplicationConfig): Algebra[F] = {
    val getOneFrameDataRepository = new GetOneFrameDataRepository(client, config)
    new OneFrameRateInterpreter(getOneFrameDataRepository.get(_))
  }
}
