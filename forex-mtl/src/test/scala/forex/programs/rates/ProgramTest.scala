package forex.programs.rates

import cats.effect.IO
import forex.domain.Rate.Pair
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.programs.rates.Protocol.GetRatesRequest
import forex.programs.rates.errors.Error
import forex.services.RatesService
import forex.services.valuecache.{Algebra => CacheAlgebra}
import org.scalatest.funspec.AnyFunSpec

import java.time.OffsetDateTime

class TestCache() extends CacheAlgebra[IO, List[Rate]] {

  override def getOrSet[A](key: String, fetchValue: IO[Either[A, List[Rate]]]): IO[Either[A, List[Rate]]] =
    fetchValue
}

class ProgramTest extends AnyFunSpec {

  val time = OffsetDateTime.parse("2022-01-11T07:48:09.94Z")

  val rate1 = Rate(Pair(Currency.AUD, Currency.USD), Price(1.0), Timestamp(time))
  val rate2 = Rate(Pair(Currency.USD, Currency.AUD), Price(2.0), Timestamp(time))
  val rate3 = Rate(Pair(Currency.JPY, Currency.USD), Price(3.0), Timestamp(time))
  val rate4 = Rate(Pair(Currency.USD, Currency.JPY), Price(4.0), Timestamp(time))
  val rate5 = Rate(Pair(Currency.JPY, Currency.AUD), Price(5.0), Timestamp(time))
  val rate6 = Rate(Pair(Currency.AUD, Currency.JPY), Price(6.0), Timestamp(time))

  val rates = List(rate1, rate2, rate3, rate4, rate5, rate6)

  describe("Program") {
    it("Should get the requested rate from the rates service, then cache the rates") {

      val ratesService: RatesService[IO] = _ => IO(Right(rates))

      var didSetCache = false

      val testCache: CacheAlgebra[IO, List[Rate]] = new CacheAlgebra[IO, List[Rate]] {
        def getOrSet[A](key: String, fetchValue: IO[Either[A, List[Rate]]]): IO[Either[A, List[Rate]]] = {
          didSetCache = true
          fetchValue
        }
      }

      val program = new Program[IO](ratesService, testCache)

      val result = program.get(GetRatesRequest(Currency.AUD, Currency.USD)).unsafeRunSync()

      val expected = Right(rate1)

      assert(result == expected)
      assert(didSetCache)
    }

    it("Should return an empty response error given OneFrame returns empty data") {
      val emptyRatesService: RatesService[IO] = _ => IO { Right(Nil) }

      val testCache = new TestCache

      val program = new Program[IO](emptyRatesService, testCache)

      val result = program.get(GetRatesRequest(Currency.AUD, Currency.USD)).unsafeRunSync()

      val expected = Left(Error.ResponseEmpty("Service returned an empty data set."))

      assert(result == expected)
    }
  }
}
