package forex.services.valuecache

trait Algebra[F[_], O] {
  def cache(value: O): F[Any]

  def get: F[Option[O]]
}
