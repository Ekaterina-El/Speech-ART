package el.ka.speechart.service.model

import el.ka.speechart.other.UserRole

data class User(
  var uid: String = "",
  val role: UserRole = UserRole.STUDY,
  val email: String = "",
  val fullName: String = "",

  // for Specialist
  val rating: Float = 0f,
  val reviews: List<String> = listOf(),

  // for Study
  val resultsOfCompleted: List<String> = listOf(),
  val score: Int = 0,
)
