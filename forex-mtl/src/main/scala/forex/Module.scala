package forex

import cats.effect.{Concurrent, Timer}
import forex.config.ApplicationConfig
import forex.domain.Rate
import forex.http.rates.RatesHttpRoutes
import forex.services._
import forex.programs._
import org.http4s._
import org.http4s.client.Client
import org.http4s.implicits._
import org.http4s.server.middleware.{AutoSlash, Timeout}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

class Module[F[_]: Concurrent: Timer](config: ApplicationConfig, client: Client[F]) {

  private val ratesService: RatesService[F] = RatesServices.oneFrameRateInterpreter[F](client, config)

  private val ratesCacheService: CacheService[F, List[Rate]] = CacheServices.guavaCacheInterpreter[F, List[Rate]](Duration.apply(4.5, TimeUnit.MINUTES))

  private val ratesProgram: RatesProgram[F] = RatesProgram[F](ratesService, ratesCacheService)

  private val ratesHttpRoutes: HttpRoutes[F] = new RatesHttpRoutes[F](ratesProgram).routes

  type PartialMiddleware = HttpRoutes[F] => HttpRoutes[F]
  type TotalMiddleware   = HttpApp[F] => HttpApp[F]

  private val routesMiddleware: PartialMiddleware = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    }
  }

  private val appMiddleware: TotalMiddleware = { http: HttpApp[F] =>
    Timeout(config.http.timeout)(http)
  }

  private val http: HttpRoutes[F] = ratesHttpRoutes

  val httpApp: HttpApp[F] = appMiddleware(routesMiddleware(http).orNotFound)

}
