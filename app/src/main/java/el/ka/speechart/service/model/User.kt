package el.ka.speechart.service.model

import el.ka.speechart.other.UserRole

data class User(
  var uid: String = "",
  val role: UserRole = UserRole.STUDY,
  val email: String = "",
  val fullName: String = "",
  var profileUrl: String = "",

  // for Specialist
  val rating: Float = 0f,
  val reviews: List<String> = listOf(),
  var description: String = "",

  // for Study
  val resultsOfCompleted: List<String> = listOf(),
  val score: Int = 0,
)

fun List<User>.filterByFullNameAndEmail(filter: String) = this.filter {
  it.fullName.contains(filter, ignoreCase = true) ||
      it.email.contains(filter, ignoreCase = true)
}