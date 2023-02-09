package el.ka.speechart.service.model

import android.content.Context
import el.ka.speechart.other.LevelOfDifficulty

data class Exercise(
  var id: String = "",
  val levelOfDifficulty: LevelOfDifficulty = LevelOfDifficulty.EASY,
  val referencePronunciationFileUrl: String = "",
  val name: String = "",
  val description: String = "",
  val text: String = "",
  var createdByASpecialist: String = "",
) {
  override fun toString(): String =
    "id: $id | name: $name | description: $description | text: $text | " +
        "level: $levelOfDifficulty | fileUrl: $referencePronunciationFileUrl | specialistUID: $createdByASpecialist"
}


fun List<Exercise>.filterBy(context: Context, search: String) = this.filter {
  it.name.contains(search, true) ||
      context.getString(it.levelOfDifficulty.strRes).contains(search, true)
}