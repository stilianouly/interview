package forex.services.rates.interpreters.oneframe

import forex.domain.Currency

class GetOneFrameRatesUriTest extends org.scalatest.funspec.AnyFunSpec {

  describe("GetOneFrameRatesUri") {
    it("Should construct a OneFrame compatible uri") {
      val result = GetOneFrameRatesUri("oneframe.com", "8080", Currency.CAD, Currency.AUD)

      assert(result.renderString === "oneframe.com:8080/rates?pair=CADAUD")
    }
  }
}
