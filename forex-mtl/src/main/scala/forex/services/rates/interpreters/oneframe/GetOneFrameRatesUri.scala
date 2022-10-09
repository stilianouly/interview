package forex.services.rates.interpreters.oneframe

import forex.domain.Currency
import forex.domain.Currency._
import org.http4s.Uri
import cats.implicits.toShow
import org.http4s.Uri.RegName

object GetOneFrameRatesUri {

  private val allCurrenciesPaired: List[(Currency, Currency)] = {
    val allCurrencies = List(AUD, CAD, CHF, EUR, GBP, NZD, JPY, SGD, USD)
    allCurrencies.flatMap(currency1 => allCurrencies.map(currency2 => (currency1, currency2)))
  }

  def apply(host: String, maybePort: Option[Int]): Uri = {
    val uri =
      new Uri(Some(Uri.Scheme.http), Some(Uri.Authority(host = RegName(host), port = maybePort)), path = "/rates")

    uri.withQueryParam("pair", allCurrenciesPaired.map {
      case (currencyA, currencyB) => s"${currencyA.show}${currencyB.show}"
    })
  }
}
