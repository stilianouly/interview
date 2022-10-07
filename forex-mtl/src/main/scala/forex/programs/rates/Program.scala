package forex.programs.rates

import cats.data.EitherT
import cats.{Applicative, Monad}
import forex.domain._
import forex.programs.rates.Program.getRequestedRate
import forex.programs.rates.Protocol.GetRatesRequest
import forex.programs.rates.errors._
import forex.services.{RatesService, ValueCacheService}

class Program[F[_]: Monad](
    ratesService: RatesService[F],
    valueCacheService: ValueCacheService[F, List[Rate]]
) extends Algebra[F] {

  override def get(request: Protocol.GetRatesRequest): F[Error Either Rate] = {

    val oneFrameResponseProgram : F[Error Either Rate] = {
      val maybeErrorOrRates: EitherT[F, Error, List[Rate]] = EitherT(
        ratesService.get(Rate.Pair(request.from, request.to))
      ).leftMap(toProgramError(_))

      valueCacheService.cache(List())

      val maybeErrorOrRate = maybeErrorOrRates.flatMap(rates => getRequestedRate(request, rates))
      maybeErrorOrRate.value
    }

    oneFrameResponseProgram
  }
}

object Program {

  def apply[F[_] : Monad](
      ratesService: RatesService[F],
      valueCacheService: ValueCacheService[F, List[Rate]]
  ): Algebra[F] = new Program[F](ratesService, valueCacheService)

  private def getRequestedRate[F[_] : Applicative](request: GetRatesRequest, rates: List[Rate]): EitherT[F, Error, Rate] = {
    val maybeRequestedRate = rates.find(rate => request.to == rate.pair.to && request.from == rate.pair.from)

    EitherT.fromOption[F](maybeRequestedRate, Error.ResponseEmpty("Service returned a missing data set"))
  }
}
