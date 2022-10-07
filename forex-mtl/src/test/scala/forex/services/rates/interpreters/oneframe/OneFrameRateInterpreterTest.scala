package forex.services.rates.interpreters.oneframe

import cats.effect.{IO, Resource}
import forex.config.{ApplicationConfig, HttpConfig, OneFrameConfig}
import forex.domain.Rate.Pair
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.services.rates.errors
import org.http4s.Response
import org.http4s.client.Client
import org.scalatest.funspec.AnyFunSpec

import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class OneFrameRateInterpreterTest extends AnyFunSpec {

  val time = OffsetDateTime.parse("2022-01-11T07:48:09.94Z")

  val validResponseBody =
    s"""
    |[
    |  {
    |    "from": "USD",
    |    "to": "JPY",
    |    "bid": 0.6118225421857174,
    |    "ask": 0.8243869101616611,
    |    "price": 0.7181047261736893,
    |    "time_stamp": "$time"
    |  }
    |,
    |
    |  {
    |    "from": "USD",
    |    "to": "JPY",
    |    "bid": 0.8435259660697864,
    |    "ask": 0.4175532166907524,
    |    "price": 0.6305395913802694,
    |    "time_stamp": "$time"
    |  }
    |]""".stripMargin

  val quoteReachedResponse =
    """{"error":"Quota reached"}""".stripMargin


  describe("OneFrameRatesInterpreter") {
    it("Should return rates given a valid response") {

      val responseStream: fs2.Stream[IO, Byte] = fs2.Stream(validResponseBody.getBytes.toIndexedSeq:_*)
      val response: Response[IO] = Response[IO](body = responseStream)
      val dummyClient = Client[IO](_ => Resource.apply[IO, Response[IO]](IO((response, IO.unit))))

      val dummyAppConfig = ApplicationConfig(HttpConfig("blah", 9090, FiniteDuration.apply(1, TimeUnit.DAYS)), OneFrameConfig("hi", None, "123"))

      val interpreter = new OneFrameRatesInterpreter[IO](dummyClient, dummyAppConfig)

      val result: Either[errors.Error, List[Rate]] = interpreter.get(Pair(Currency.AUD, Currency.USD)).unsafeRunSync()

      val expected = Right(
        List(
          Rate(Pair(Currency.USD, Currency.JPY), Price(BigDecimal(0.7181047261736893)), Timestamp(time)),
          Rate(Pair(Currency.USD, Currency.JPY), Price(BigDecimal(0.6305395913802694)), Timestamp(time))
        )
      )

      assert(result === expected)
    }
  }
}
