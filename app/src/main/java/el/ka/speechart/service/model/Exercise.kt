package el.ka.speechart.service.model

import android.content.Context
import el.ka.speechart.other.LevelOfDifficulty
import el.ka.speechart.other.MediaFileInfo

data class Exercise(
  var id: String = "",
  val levelOfDifficulty: LevelOfDifficulty = LevelOfDifficulty.EASY,
  var referencePronunciationFile: MediaFileInfo? = null,
  val name: String = "",
  val description: String = "",
  val text: String = "",
  var createdByASpecialist: String = "",
): java.io.Serializable {
  override fun toString(): String =
    "id: $id | name: $name | description: $description | text: $text | " +
        "level: $levelOfDifficulty | fileUrl: ${referencePronunciationFile?.url} | specialistUID: $createdByASpecialist"
}


fun List<Exercise>.filterBy(context: Context, search: String) = this.filter {
  it.name.contains(search, true) ||
      context.getString(it.levelOfDifficulty.strRes).contains(search, true)
}