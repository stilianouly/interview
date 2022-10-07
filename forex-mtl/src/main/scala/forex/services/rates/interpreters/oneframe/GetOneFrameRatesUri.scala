package forex.services.rates.interpreters.oneframe

import forex.domain.Currency
import forex.domain.Currency._
import org.http4s.Uri
import cats.implicits.toShow

object GetOneFrameRatesUri {

  private val allCurrenciesPaired: List[(Currency, Currency)] = {
    val allCurrencies = List(AUD, CAD, CHF, EUR, GBP, NZD, JPY, SGD, USD)
    allCurrencies.flatMap(currency1 => allCurrencies.map(currency2 => (currency1, currency2)))
  }

  private val pairParamsAsString = {
    allCurrenciesPaired.map(currencyPair => s"pair=${currencyPair._1.show}${currencyPair._2.show}").mkString("&")
  }

  def apply(host: String, maybePort: Option[Int]): Uri = {
    val port = maybePort.map(":" + _.toString).getOrElse("")

    Uri.unsafeFromString(s"$host$port/rates?$pairParamsAsString")
  }
}
