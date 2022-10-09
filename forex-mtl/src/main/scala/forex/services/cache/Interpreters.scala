package forex.services.cache

import cats.effect.Sync
import com.google.common.cache.CacheBuilder
import forex.services.cache.interpreters.guava.GuavaCacheInterpreter
import scalacache._
import scalacache.guava._

import scala.concurrent.duration.Duration

object Interpreters {
  def guavaCacheInterpreter[F[_] : Sync, O](duration: Duration): Algebra[F, O] = {
    val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[O]]
    val guavaCache = GuavaCache(underlyingGuavaCache)

    new GuavaCacheInterpreter[F, O](guavaCache, duration)
  }
}
