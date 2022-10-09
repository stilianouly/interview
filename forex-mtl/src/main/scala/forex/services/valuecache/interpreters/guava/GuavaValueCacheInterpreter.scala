package forex.services.valuecache.interpreters.guava

import cats._
import cats.data.EitherT
import cats.effect.Sync
import forex.services.valuecache.Algebra
import scalacache.guava._
import scalacache.modes.sync._
import cats.syntax.all._

import scala.concurrent.duration.Duration

class GuavaValueCacheInterpreter[F[_] : Sync, O](guavaCache: GuavaCache[O], duration: Duration) extends Algebra[F, O] {

  def getOrSet[A](key: String, fetchValue: F[A Either O]): F[A Either O] = {
    guavaCache.get(key) match {
      case Some(cachedValue) => cachedValue.asRight[A].pure[F]
      case None => EitherT(fetchValue).map(value => Functor[Id].as(guavaCache.put(key)(value, Some(duration)),value)).value
    }
  }
}
