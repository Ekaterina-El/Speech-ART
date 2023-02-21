package el.ka.speechart.service.model

import android.content.Context
import el.ka.speechart.other.LevelOfDifficulty
import el.ka.speechart.other.MediaFileInfo

data class Exercise(
  var id: String = "",

  val levelOfDifficulty: LevelOfDifficulty = LevelOfDifficulty.EASY,
  val name: String = "",
  val description: String = "",
  val text: String = "",
  var createdByASpecialist: String = "",

  val referencePronunciationAmplitude: List<Int> = listOf(),
  var referencePronunciationUrl: String? =  "",
): java.io.Serializable {
  override fun toString(): String =
    "id: $id | name: $name | description: $description | text: $text | " +
        "level: $levelOfDifficulty | fileUrl: $referencePronunciationUrl | specialistUID: $createdByASpecialist"
}


fun List<Exercise>.filterBy(context: Context, search: String) = this.filter {
  it.name.contains(search, true) ||
      context.getString(it.levelOfDifficulty.strRes).contains(search, true)
}