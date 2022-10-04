package forex.services.rates.interpreters

import cats.effect.IO
import forex.domain.Rate.Pair
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.repository.oneframe.Protocol.OneFrameRateResponseData
import forex.services.rates.errors.Error.OneFrameResponseEmpty
import org.scalatest.funspec.AnyFunSpec

import java.time.OffsetDateTime

class OneFrameRateInterpreterTest extends AnyFunSpec {

  val time = OffsetDateTime.now()

  describe("OneFrameRateInterpreter") {
    it("Should return a rate given a valid response") {

      val getOneFrameDataStub: Rate.Pair => IO[List[OneFrameRateResponseData]] = {
        _ => IO.pure(List(OneFrameRateResponseData(
          from = "AUD", to = "USD", bid = 123, ask = 456, price = 321, timestamp = time))
        )
      }

      val interpreter = new OneFrameRateInterpreter[IO](getOneFrameDataStub)

      val result = interpreter.get(Pair(Currency.AUD, Currency.USD)).unsafeRunSync()

      val expected = Right(Rate(Pair(Currency.AUD, Currency.USD), Price(BigDecimal(321)), Timestamp(time)))

      assert(result === expected)
    }

    it("Should fail with empty response error given an empty response") {

      val getOneFrameDataEmptyStub: Rate.Pair => IO[List[OneFrameRateResponseData]] = {
        _ => IO.pure(List())
      }

      val interpreter = new OneFrameRateInterpreter[IO](getOneFrameDataEmptyStub)

      val result = interpreter.get(Pair(Currency.AUD, Currency.USD)).unsafeRunSync()

      val expected = Left(OneFrameResponseEmpty("OneFrame returned an empty data set"))

      assert(result === expected)
    }
  }

}
