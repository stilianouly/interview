package forex.repository

import cats.effect.Sync
import cats.implicits.toFunctorOps
import com.google.common.cache.CacheBuilder
import scalacache._
import scalacache.guava._
import scalacache.modes.sync._

import scala.concurrent.duration.Duration

class RepositoryCache[F[_] : Sync, I, O](repository: I => F[O], duration: Duration) {

  val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[O]]

  val cache = GuavaCache(underlyingGuavaCache)

  def get(input: I): F[O] = {

    val maybeCachedItem = cache.get(input)

    maybeCachedItem match {
      case Some(value) => Sync[F].pure(value)
      case None => {
        val repositoryResultProgram = repository(input)
        repositoryResultProgram.map(repositoryResult => {
          cache.put(input)(repositoryResult, Some(duration))
          repositoryResult
        })
      }
    }
  }
}
