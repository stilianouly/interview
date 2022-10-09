package forex.services.cache.interpreters.guava

import cats.effect.IO
import com.google.common.cache.CacheBuilder
import org.scalatest.funspec.AnyFunSpec
import scalacache.Entry
import scalacache.guava.GuavaCache

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

class GuavaCacheInterpreterTest extends AnyFunSpec {

  describe("GuavaCache") {
    it("Should cache the value and return the value when the cache is empty") {
      val duration = Duration.apply(4.5, TimeUnit.MINUTES)
      val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[Int]]
      val guavaCache = GuavaCache(underlyingGuavaCache)

      val guavaValueCacheInterpreter = new GuavaCacheInterpreter[IO, Int](guavaCache, duration = duration)

      val fetchValue = IO(Right(1))

      val result = guavaValueCacheInterpreter.getOrSet("key", fetchValue).unsafeRunSync()

      assert(result == Right(1))
    }

    it("Should get the value from the cache and return the value when the cache is not empty") {
      val duration = Duration.apply(4.5, TimeUnit.MINUTES)
      val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[Int]]
      val guavaCache = GuavaCache(underlyingGuavaCache)

      val guavaValueCacheInterpreter = new GuavaCacheInterpreter[IO, Int](guavaCache, duration = duration)

      val fetchValueA = IO(Right(1))
      guavaValueCacheInterpreter.getOrSet("key", fetchValueA).unsafeRunSync()

      val fetchValueB = IO(Right(2))
      val result = guavaValueCacheInterpreter.getOrSet("key", fetchValueB).unsafeRunSync()

      assert(result == Right(1))
    }
  }
}
