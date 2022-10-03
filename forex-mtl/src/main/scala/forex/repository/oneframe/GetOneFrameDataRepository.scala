package forex.repository.oneframe

import cats.effect.Sync
import forex.config.ApplicationConfig
import forex.domain.Rate
import forex.repository.oneframe.Protocol.OneFrameRateResponseData
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client
import org.http4s.{Header, Headers, Request}

class GetOneFrameDataRepository[F[_] : Sync](client: Client[F], config: ApplicationConfig) {

  def get(pair: Rate.Pair): F[List[OneFrameRateResponseData]] ={
    val oneFrameUri = GetOneFrameRatesUri(config.oneframe.host, config.oneframe.port, pair.from, pair.to)

    val request = Request[F](uri = oneFrameUri, headers = Headers.of(Header("token", config.oneframe.token)))

    val oneFrameResponse: F[List[OneFrameRateResponseData]] = client.expect[List[OneFrameRateResponseData]](request)

    oneFrameResponse
  }
}
