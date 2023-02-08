package el.ka.speechart.service.model

import el.ka.speechart.other.LevelOfDifficulty

data class Exercise(
  val id: String = "",
  val levelOfDifficulty: LevelOfDifficulty = LevelOfDifficulty.EASY,
  val referencePronunciationFileUrl: String = "",
  val name: String = "",
  val description: String = "",
  val text: String = "",
  val createdByASpecialist: String = "",
)
