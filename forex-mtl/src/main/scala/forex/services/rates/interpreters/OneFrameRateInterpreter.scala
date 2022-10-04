package forex.services.rates.interpreters

import cats.Applicative
import cats.implicits.toFunctorOps
import forex.domain.Rate
import forex.repository.oneframe.Protocol._
import forex.services.rates.Algebra
import forex.services.rates.Converters._
import forex.services.rates.errors._

class OneFrameRateInterpreter[F[_]: Applicative](getOneFrameData: Rate.Pair => F[List[OneFrameRateResponseData]]) extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] = {

    val oneFrameResponseProgram : F[List[OneFrameRateResponseData]] = getOneFrameData(pair)

    oneFrameResponseProgram.map(getFirstItemOrFail(_).map(_.asRate))
  }

  private def getFirstItemOrFail[A](list: List[A]): Error Either A = {
    val element = (list.headOption.toRight(Error.OneFrameResponseEmpty("OneFrame returned an empty data set")))
    element
  }
}
