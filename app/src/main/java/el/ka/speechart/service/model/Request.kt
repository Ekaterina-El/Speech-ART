package el.ka.speechart.service.model

data class RequestToRegSpecialist(
  val id: String = "",
  val userData: User = User(),
  val password: String = ""
)