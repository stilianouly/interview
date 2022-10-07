package forex.services.rates.interpreters.oneframe

import cats.effect.Sync
import cats.implicits._
import forex.config.ApplicationConfig
import forex.domain.Rate
import forex.services.rates.Algebra
import forex.services.rates.errors._
import forex.services.rates.interpreters.oneframe.Converters.OneFrameRateResponseDataOps
import forex.services.rates.interpreters.oneframe.Protocol._
import io.circe
import org.http4s.client.Client
import org.http4s.{Header, Headers, Request, Response}
import io.circe.Decoder.decodeList
import io.circe.parser

class OneFrameRatesInterpreter[F[_] : Sync](client: Client[F], config: ApplicationConfig) extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either List[Rate]] = {
    val oneFrameUri = GetOneFrameRatesUri(config.oneframe.host, config.oneframe.port)

    val request = Request[F](uri = oneFrameUri, headers = Headers.of(Header("token", config.oneframe.token)))

    val oneFrameResponse = Sync[F].pure(request).flatMap(client.run(_).use(responseHandler))

    oneFrameResponse.map {
      case Left(_) => Left(Error.OneFrameLookupFailed("Unexpected upstream response from OneFrame service. The rate limit may have been exceeded."))
      case Right(rates) => {
        if (rates.map(_.asRate).contains(None))
          Left(Error.OneFrameLookupFailed("Unexpected currency response."))
        else
          Right(rates.flatMap(_.asRate))
      }
    }
  }

  private def responseHandler(response: Response[F]): F[circe.Error Either List[OneFrameRateResponseData]] = {
    val responseString = response.as[String]

    val decodingResult = responseString.map(parser.decode[List[OneFrameRateResponseData]](_))

    decodingResult
  }
}
