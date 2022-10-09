package forex

package object services {
  type RatesService[F[_]] = rates.Algebra[F]
  final val RatesServices = rates.Interpreters

  type CacheService[F[_], O] = cache.Algebra[F, O]
  final val CacheServices = cache.Interpreters
}
