package el.ka.speechart.service.model

data class Review(
  var id: String = "",

  val userId: String = "",
  var userLocal: User? = null,

  val specialistId: String = "",
  var specialistLocal: User? = null,

  val performedExerciseId: String = "",
  var performedExerciseLocal: PerformedExercise? = null,

  val rating: Int = 0,
  val text: String = "",
)
