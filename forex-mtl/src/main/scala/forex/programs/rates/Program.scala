package forex.programs.rates

import cats.data.EitherT
import cats.implicits.{catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
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

  override def get(request: Protocol.GetRatesRequest): F[Error Either Rate] = {

    val errorOrRatesProgram: F[Either[ServiceError, List[Rate]]] = valueCacheService.get.flatMap {
      case Some(cachedRates) => Monad[F].pure(Right(cachedRates))
      case None => {
        ratesService.get(Rate.Pair(request.from, request.to)).flatMap {
          case Right(ratesFromService) => valueCacheService.cache(ratesFromService).map(_ => Right(ratesFromService))
          case error => error.pure[F]
        }
      }
    }

    EitherT(errorOrRatesProgram).leftMap(toProgramError).flatMap(rates => getRequestedRate(request, rates)).value
  }
}

object Program {
  def apply[F[_] : Monad](
      ratesService: RatesService[F],
      valueCacheService: ValueCacheService[F, List[Rate]]
  ): Algebra[F] = new Program[F](ratesService, valueCacheService)

  def getRequestedRate[F[_] : Applicative](request: GetRatesRequest, rates: List[Rate]): EitherT[F, Error, Rate] = {
    val maybeRequestedRate = rates.find(rate => request.to == rate.pair.to && request.from == rate.pair.from)

    EitherT.fromOption[F](maybeRequestedRate, Error.ResponseEmpty("Service returned a missing data set"))
  }
}
