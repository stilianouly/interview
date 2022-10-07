package forex.services.rates

import cats.effect.Sync
import forex.config.ApplicationConfig
import forex.services.rates.interpreters.oneframe.OneFrameRatesInterpreter
import org.http4s.client.Client

object Interpreters {
  def oneFrameRateInterpreter[F[_]: Sync](client: Client[F], config: ApplicationConfig): Algebra[F] = {
      new OneFrameRatesInterpreter(client, config)
  }
}
