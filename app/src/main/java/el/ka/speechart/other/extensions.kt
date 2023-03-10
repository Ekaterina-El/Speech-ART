package el.ka.speechart.other

import android.media.MediaPlayer
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

fun Spinner.addListener(onSelected: (Any?) -> Unit) {
  this.onItemSelectedListener =
    object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        onSelected(p0!!.selectedItem)
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {}
    }
}

fun Int.toMinutesAndSeconds(): String {
  val minutes = this / 60
  val seconds = this - minutes * 60
  return "${minutes.addZero()}:${seconds.addZero()}"
}

fun Int.addZero(): String = if (this < 10) "0$this" else "$this"

val MediaPlayer.progress: Int get() = (this.currentPosition.toFloat() / this.duration.toFloat() * 100).toInt()
fun MediaPlayer.playAt(progress: Int) {
  val playPosition = (this.duration / 100) * progress
  this.seekTo(playPosition)
}

