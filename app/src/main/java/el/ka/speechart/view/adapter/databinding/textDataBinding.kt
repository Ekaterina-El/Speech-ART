package el.ka.speechart.view.adapter.databinding

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import el.ka.speechart.R
import el.ka.speechart.other.LevelOfDifficulty
import el.ka.speechart.other.MediaFileInfo
import el.ka.speechart.other.toMinutesAndSeconds
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.model.User

@BindingAdapter("app:text")
fun showText(textView: TextView, text: String?) {
  hideView(textView, if (text == "") null else text)
  textView.text = text
}

@BindingAdapter("app:hideIfEmpty")
fun hideView(view: View, value: Any?) {
  view.visibility = if (value == null) View.GONE else View.VISIBLE
}

@BindingAdapter("app:time")
fun showTime(textView: TextView, time: Int?) {
  if (time == null) {
    textView.text = ""
    return
  }
  textView.text = (time / 1000).toMinutesAndSeconds()
}

@BindingAdapter("app:levelExercise")
fun showExerciseLevel(textView: TextView, levelOfDifficulty: LevelOfDifficulty?) {
  if (levelOfDifficulty == null) return
  textView.text = textView.context.getString(levelOfDifficulty.strRes)
}

@BindingAdapter("app:levelDifficulty")
fun showLevelDifficulty(textView: TextView, exercise: Exercise?) {
  val strRes = when(exercise) {
    null -> R.string.exercise_deleted
    else -> exercise.levelOfDifficulty.strRes
  }
  textView.text = textView.context.getString(strRes)
}

@BindingAdapter("app:exerciseName")
fun showExerciseName(textView: TextView, exercise: Exercise?) {
  val str = when(exercise) {
    null -> textView.context.getString(R.string.exercise_deleted)
    else -> exercise.name
  }
  textView.text = str
}

@BindingAdapter("app:exerciseDescription")
fun showExerciseDescription(textView: TextView, exercise: Exercise?) {
  val str = when(exercise) {
    null -> textView.context.getString(R.string.no_data)
    else -> exercise.description
  }
  textView.text = str
}

@BindingAdapter("app:exerciseText")
fun showExerciseText(textView: TextView, exercise: Exercise?) {
  val str = when(exercise) {
    null -> textView.context.getString(R.string.no_data)
    else -> exercise.text
  }
  textView.text = str
}

@BindingAdapter("app:userName")
fun showUserName(textView: TextView, user: User?) {
  val str = when(user) {
    null -> textView.context.getString(R.string.user_deleted)
    else -> user.fullName
  }
  textView.text = str
}

@BindingAdapter("app:userEmail")
fun showUserEmail(textView: TextView, user: User?) {
  val str = when(user) {
    null -> textView.context.getString(R.string.no_data)
    else -> user.email
  }
  textView.text = str
}

@BindingAdapter("app:userNameScore")
fun showUserNameAndScore(textView: TextView, user: User?) {
  val str = when(user) {
    null -> textView.context.getString(R.string.no_data)
    else -> textView.context.getString(R.string.performed_exercises_user_info, user.fullName, user.score)
  }
  textView.text = str
}

@BindingAdapter("app:fileName")
fun showFileName(textView: TextView, file: MediaFileInfo?) {
  textView.text = when (file) {
    null -> textView.context.getString(R.string.file_no_picked)
    else -> "${file.title} | ${file.duration.toMinutesAndSeconds()}"
  }
}