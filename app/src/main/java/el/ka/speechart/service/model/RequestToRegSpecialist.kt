package el.ka.speechart.service.model

data class RequestToRegSpecialist(
  var id: String = "",
  val userData: User = User(),
  val password: String = "",
  val email: String = "",
)

fun List<RequestToRegSpecialist>.filterByUser(search: String) = this.filter {
  it.userData.email.contains(search, ignoreCase = true) ||
      it.userData.fullName.contains(search, true)
}