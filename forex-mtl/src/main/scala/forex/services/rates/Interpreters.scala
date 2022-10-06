package forex.services.rates

import cats.Applicative
import cats.effect.Sync
import forex.config.ApplicationConfig
import forex.domain.Rate
import forex.repository.RepositoryCache
import forex.repository.oneframe.{GetOneFrameDataRepository, Protocol}
import interpreters._
import org.http4s.client.Client

import scala.concurrent.duration.{Duration, MINUTES}

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = new OneFrameDummy[F]()

  def oneFrameRateInterpreter[F[_]: Sync](client: Client[F], config: ApplicationConfig): Algebra[F] = {
    val getOneFrameDataRepository: GetOneFrameDataRepository[F] = new GetOneFrameDataRepository(client, config)
    val getOneFrameDataRepositoryCache: RepositoryCache[F, Rate.Pair, List[Protocol.OneFrameRateResponseData]] = new RepositoryCache(getOneFrameDataRepository.get(_), Duration(4.5, MINUTES))
    new OneFrameRateInterpreter(getOneFrameDataRepositoryCache.get(_))
  }
}
