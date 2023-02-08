package el.ka.speechart.view.adapter.databinding

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:text")
fun showText(textView: TextView, text: String?) {
  hideView(textView, if (text == "") null else text)
  textView.text = text
}

@BindingAdapter("app:hideIfEmpty")
fun hideView(view: View, value: Any?) {
  view.visibility = if (value == null) View.GONE else View.VISIBLE
}

