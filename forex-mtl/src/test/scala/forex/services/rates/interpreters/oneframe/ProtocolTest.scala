package forex.services.rates.interpreters.oneframe

import forex.services.rates.interpreters.oneframe.Protocol._
import io.circe.parser

import java.time.OffsetDateTime

class ProtocolTest extends org.scalatest.funspec.AnyFunSpec {

  val examplePayload = """[
    {
      "from" : "USD",
      "to" : "JPY",
      "bid" : 0.3538208472273735,
      "ask" : 0.2326483104404954,
      "price" : 0.29323457883393445,
      "time_stamp" : "2022-10-02T23:34:37.365Z"
    }
  ]"""

  val exampleRate =
    List(
      OneFrameRateResponseData(
        from = "USD",
        to = "JPY",
        bid = 0.3538208472273735,
        ask = 0.2326483104404954,
        price = 0.29323457883393445,
        timestamp = OffsetDateTime.parse("2022-10-02T23:34:37.365Z")
      )
    )

  describe("oneFrameRateResponseDataDecoder") {
    it("Should decode a list of valid OneFrame rate response data") {

      val result = parser.decode[List[OneFrameRateResponseData]](examplePayload)

      assert(result === Right(exampleRate))

    }
  }
}
