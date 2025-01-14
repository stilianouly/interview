package forex.services.rates.interpreters.oneframe

import forex.domain.Rate.Pair
import forex.domain.{ Currency, Price, Rate, Timestamp }
import forex.services.rates.interpreters.oneframe.Protocol.OneFrameRateResponseData

object Converters {

  private[rates] implicit class OneFrameRateResponseDataOps(val oneFrameRateResponseData: OneFrameRateResponseData)
      extends AnyVal {
    def asRate: Option[Rate] = {
      for {
        from <- Currency.fromString(oneFrameRateResponseData.from)
        to <- Currency.fromString(oneFrameRateResponseData.to)
      } yield Rate(
        Pair(from, to),
        Price(oneFrameRateResponseData.price),
        Timestamp(oneFrameRateResponseData.timestamp)
      )
    }
  }
}
