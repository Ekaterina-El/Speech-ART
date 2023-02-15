package el.ka.speechart.service.model

data class PerformedExercise(
  val id: String = "",
  val user: String = "",
  val userAudioFileUrl: String = "",

  val exerciseId: String = "",
  val exerciseLocal: Exercise? = null,

  val specialistId: String? = null, // null if no check
  val specialistAnswer: String? = null,

  val reviewId: String? = null
)