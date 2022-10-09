package forex.services.valuecache

trait Algebra[F[_], O] {

  def getOrSet[A](key: String, value: F[A Either O]): F[A Either O]
}
