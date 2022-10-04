package forex.services.rates.interpreters

import cats.Applicative
import cats.effect.Sync
import cats.implicits.toFunctorOps
import forex.domain.Rate
import forex.services.rates.Converters._
import forex.repository.oneframe.Protocol._
import forex.services.rates.Algebra
import forex.services.rates.errors._

class OneFrameRateInterpreter[F[_]: Applicative : Sync](getOneFrameData: Rate.Pair => F[List[OneFrameRateResponseData]]) extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] = {

    val oneFrameResponse: F[List[OneFrameRateResponseData]] = getOneFrameData(pair)

    //Check if returned list is empty
    //Check if external F fails
    //Cache?

    oneFrameResponse.map(oneFrameRateResponseDatas => Right(oneFrameRateResponseDatas.map(_.asRate).head))
  }
}
