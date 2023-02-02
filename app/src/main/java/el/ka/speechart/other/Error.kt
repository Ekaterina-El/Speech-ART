package el.ka.speechart.other

import el.ka.speechart.R

data class ErrorApp(val messageRes: Int)

object Errors {
  val invalidEmailPassword = ErrorApp(R.string.invalid_email_password)
  val unknown = ErrorApp(R.string.unknown_error)
  val network = ErrorApp(R.string.network_error)
  val weakPassword = ErrorApp(R.string.weak_password)
  val userCollision = ErrorApp(R.string.user_collision)
}