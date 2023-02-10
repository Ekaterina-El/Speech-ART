package el.ka.speechart.view.adapter.databinding

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import el.ka.speechart.other.LevelOfDifficulty
import el.ka.speechart.other.toMinutesAndSeconds

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
  textView.text = time?.toMinutesAndSeconds() ?: ""
}

@BindingAdapter("app:levelExercise")
fun showExerciseLevel(textView: TextView, levelOfDifficulty: LevelOfDifficulty?) {
  if (levelOfDifficulty == null) return
  textView.text = textView.context.getString(levelOfDifficulty.strRes)
}

