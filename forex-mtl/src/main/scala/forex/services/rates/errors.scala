package forex.services.rates

object errors {

  sealed trait Error
  object Error {
    final case class OneFrameLookupFailed(msg: String) extends Error
    final case class OneFrameResponseEmpty(msg: String) extends Error
  }

}
