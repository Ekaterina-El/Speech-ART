package el.ka.speechart.service.model

import android.content.Context

data class PerformedExercise(
  var id: String = "",
  val user: String = "",
  var userLocal: User? = null,

  val userAudioFileUrl: String = "",

  val exerciseId: String = "",
  var exerciseLocal: Exercise? = null,

  var specialistId: String? = null, // null if no check
  var specialistLocal: User? = null,
  var specialistAnswer: String? = null,

  var reviewId: String? = null,
  var reviewLocal: Review? = null,
  val userAmplitude: List<Int>? = listOf()
)


fun List<PerformedExercise>.filterBy(context: Context, search: String) = this.filter {
  it.exerciseLocal!!.name.contains(search, true) ||
      context.getString(it.exerciseLocal!!.levelOfDifficulty.strRes).contains(search, true) ||
      it.userLocal!!.fullName.contains(search, true) ||
      it.userLocal!!.email.contains(search, true)
}