package forex.http
package rates

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Sync
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxTuple2Semigroupal}
import cats.syntax.flatMap._
import forex.programs.RatesProgram
import forex.programs.rates.errors.Error.{RateLookupFailed, ResponseEmpty}
import forex.programs.rates.{Protocol => RatesProgramProtocol}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

class RatesHttpRoutes[F[_]: Sync](rates: RatesProgram[F]) extends Http4sDsl[F] {

  import Protocol._
  import QueryParams._

  private[http] val prefixPath = "/rates"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? FromQueryParam(fromInput) +& ToQueryParam(toInput) =>

      val validatedInputs = (fromInput, toInput).mapN((fromCurrency, toCurrency) => (fromCurrency, toCurrency))
      validatedInputs match {
        case Valid((from, to)) =>
          rates.get(RatesProgramProtocol.GetRatesRequest(from, to)).attempt.flatMap {
            case Left(effectError) => InternalServerError(effectError.getMessage)
            case Right(domainError) => domainError match {
              case Left(RateLookupFailed(msg)) => InternalServerError(msg)
              case Left(ResponseEmpty(msg)) => NotFound(msg)
              case Right(rate) => Ok(rate)
            }
          }
        case Invalid(e) => BadRequest(e.toList.mkString("\n"))
      }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
