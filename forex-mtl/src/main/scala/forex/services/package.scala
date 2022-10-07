package forex

package object services {
  type RatesService[F[_]] = rates.Algebra[F]
  final val RatesServices = rates.Interpreters

  type ValueCacheService[F[_], O] = valuecache.Algebra[F, O]
  final val ValueCacheServices = valuecache.Interpreters
}
