package forex.repository.oneframe

import io.circe.Decoder
import java.time.OffsetDateTime

object Protocol {

  final case class OneFrameRateResponseData(
    from: String,
    to: String,
    bid: BigDecimal,
    ask: BigDecimal,
    price: BigDecimal,
    timestamp: OffsetDateTime
  )

  implicit val oneFrameRateResponseDataDecoder: Decoder[OneFrameRateResponseData] =
    Decoder.forProduct6(
      "from",
      "to",
      "bid",
      "ask",
      "price",
      "time_stamp")(
        (from,to,bid,ask,price,timestamp: String) => OneFrameRateResponseData(from, to, bid, ask, price, OffsetDateTime.parse(timestamp))
      )
}
