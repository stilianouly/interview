package forex.programs.rates

import cats.data.EitherT
import cats.{Applicative, Monad}
import forex.domain._
import forex.programs.rates.Program.getRequestedRate
import forex.programs.rates.Protocol.GetRatesRequest
import forex.programs.rates.errors._
import forex.services.rates.errors.{Error => ServiceError}
import forex.services.{RatesService, ValueCacheService}

class Program[F[_]: Monad](
    ratesService: RatesService[F],
    valueCacheService: ValueCacheService[F, List[Rate]]
) extends Algebra[F] {

  final private val key = "rates"

  override def get(request: Protocol.GetRatesRequest): F[Error Either Rate] = {
    val errorOrRatesEffect: F[Either[ServiceError, List[Rate]]] =
      valueCacheService.getOrSet(key, ratesService.get(Rate.Pair(request.from, request.to)))

    EitherT(errorOrRatesEffect).leftMap(toProgramError).flatMap(rates => getRequestedRate(request, rates)).value
  }
}

object Program {
  def apply[F[_] : Monad](
      ratesService: RatesService[F],
      valueCacheService: ValueCacheService[F, List[Rate]]
  ): Algebra[F] = new Program[F](ratesService, valueCacheService)

  def getRequestedRate[F[_] : Applicative](request: GetRatesRequest, rates: List[Rate]): EitherT[F, Error, Rate] = {
    val maybeRequestedRate = rates.find(rate => request.to == rate.pair.to && request.from == rate.pair.from)

    EitherT.fromOption[F](maybeRequestedRate, Error.ResponseEmpty("Service returned an empty data set."))
  }
}
