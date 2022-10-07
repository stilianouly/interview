package forex.services.rates.interpreters.oneframe

import cats.data.Nested
import cats.effect.Sync
import cats.implicits._
import forex.config.ApplicationConfig
import forex.domain.Rate
import forex.services.rates.Algebra
import forex.services.rates.errors._
import forex.services.rates.interpreters.oneframe.Converters.OneFrameRateResponseDataOps
import forex.services.rates.interpreters.oneframe.Protocol.OneFrameRateResponseData
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.{Header, Headers, Request}

class OneFrameRatesInterpreter[F[_] : Sync](client: Client[F], config: ApplicationConfig) extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either List[Rate]] ={
    val oneFrameUri = GetOneFrameRatesUri(config.oneframe.host, config.oneframe.port, pair.from, pair.to)

    val request = Request[F](uri = oneFrameUri, headers = Headers.of(Header("token", config.oneframe.token)))

    val oneFrameResponse: F[List[OneFrameRateResponseData]] = client.expect[List[OneFrameRateResponseData]](request)

    //attempt
    //quota exceeded

    Nested(oneFrameResponse).map(_.asRate).value.map(Right(_))
  }
}
