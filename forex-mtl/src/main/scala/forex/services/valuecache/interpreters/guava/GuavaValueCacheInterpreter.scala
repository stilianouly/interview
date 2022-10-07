package forex.services.valuecache.interpreters.guava

import cats.effect.Sync
import cats.implicits.catsSyntaxApplicativeId
import forex.services.valuecache.Algebra
import scalacache.guava._
import scalacache.modes.sync._

import scala.concurrent.duration.Duration

class GuavaValueCacheInterpreter[F[_] : Sync, O](guavaCache: GuavaCache[O], duration: Duration) extends Algebra[F, O] {

  def cache(value: O): F[Any] = {
    guavaCache.put(())(value, Some(duration)).pure[F]
  }

  def get: F[Option[O]] = {
    guavaCache.get(()).pure[F]
  }
}
