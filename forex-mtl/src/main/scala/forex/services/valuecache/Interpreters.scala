package forex.services.valuecache

import cats.effect.Sync
import com.google.common.cache.CacheBuilder
import forex.services.valuecache.interpreters.guava.GuavaValueCacheInterpreter
import scalacache._
import scalacache.guava._

import scala.concurrent.duration.Duration

object Interpreters {
  def guavaValueCacheInterpreter[F[_] : Sync, O](duration: Duration): Algebra[F, O] = {
    val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[O]]
    val guavaCache = GuavaCache(underlyingGuavaCache)

    new GuavaValueCacheInterpreter[F, O](guavaCache, duration)
  }
}
