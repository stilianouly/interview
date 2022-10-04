package forex.services.rates.interpreters

import cats.effect.IO
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.domain.Rate.Pair
import forex.repository.oneframe.Protocol.OneFrameRateResponseData
import org.scalatest.funspec.AnyFunSpec

import java.time.OffsetDateTime

class OneFrameRateInterpreterTest extends AnyFunSpec {

  val time = OffsetDateTime.now()

  val getOneFrameDataStub: Rate.Pair => IO[List[OneFrameRateResponseData]] = {
    _ => IO.pure(List(OneFrameRateResponseData(
      from = "AUD", to = "USD", bid = 123, ask = 456, price = 321, timestamp = time))
    )
  }

  describe("OneFrameRateInterpreter") {
    it("Should return a rate given a valid response") {
      val interpreter = new OneFrameRateInterpreter[IO](getOneFrameDataStub)

      val result = interpreter.get(Pair(Currency.AUD, Currency.USD)).unsafeRunSync().right.get

      val expected = Rate(Pair(Currency.AUD, Currency.USD), Price(BigDecimal(321)), Timestamp(time))

      assert(result === expected)
    }
  }

}
