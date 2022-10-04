package forex.services.rates

import forex.domain.Rate.Pair
import forex.domain.{ Currency, Price, Rate, Timestamp }
import forex.repository.oneframe.Protocol.OneFrameRateResponseData

object Converters {

  private[rates] implicit class OneFrameRateResponseDataOps(val oneFrameRateResponseData: OneFrameRateResponseData) extends AnyVal {
    def asRate: Rate =
      Rate(
        Pair(
          Currency.fromString(oneFrameRateResponseData.from),
          Currency.fromString(oneFrameRateResponseData.to)
        ),
        Price(oneFrameRateResponseData.price),
        Timestamp(oneFrameRateResponseData.timestamp)
      )
  }
}
