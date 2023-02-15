package el.ka.speechart.service.model

data class PerformedExercise(
  var id: String = "",
  val user: String = "",
  var userLocal: User? = null,

  val userAudioFileUrl: String = "",

  val exerciseId: String = "",
  var exerciseLocal: Exercise? = null,

  val specialistId: String? = null, // null if no check
  var specialistLocal: User? = null,
  val specialistAnswer: String? = null,

  val reviewId: String? = null
)