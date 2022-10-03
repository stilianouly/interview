package forex.repository.oneframe

import forex.domain.Currency

class GetOneFrameRatesUriTest extends org.scalatest.funsuite.AnyFunSuite {

  test("GetOneFrameRatesUri should construct a uri") {
    val result = GetOneFrameRatesUri("hi.com", "8080", Currency.CAD, Currency.AUD)

    assert(result.renderString === "hi.com:8080/rates?pair=CADAUD")
  }
}
