package el.ka.speechart.service.model

data class Exercise(
  val uid: String = "",
  val levelOfDifficulty: Int = 0,
  val referencePronunciationFileUrl: String = "",
  val description: String = "",
  val text: String = "",
  val createdByASpecialist: String = "",
)
