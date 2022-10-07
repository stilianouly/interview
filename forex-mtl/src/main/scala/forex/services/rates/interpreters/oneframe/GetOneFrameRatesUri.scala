package forex.services.rates.interpreters.oneframe

import forex.domain.Currency
import org.http4s.Uri

object GetOneFrameRatesUri {
  def apply(host: String, port: String, from: Currency, to: Currency): Uri =
    Uri.unsafeFromString(s"$host:$port/rates?pair=$from$to")
}
