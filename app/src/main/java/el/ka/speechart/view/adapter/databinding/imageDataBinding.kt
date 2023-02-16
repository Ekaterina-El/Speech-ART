package el.ka.speechart.view.adapter.databinding

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import androidx.databinding.BindingAdapter
import el.ka.speechart.R
import el.ka.speechart.other.ImageLoader
import el.ka.speechart.other.LevelOfDifficulty
import el.ka.speechart.other.Status

@BindingAdapter("app:imageUrl")
fun setImageByUrl(image: ImageView, url: String?) {
  ImageLoader.getInstance(image.context).loadTo(url ?: "", image)
}

@BindingAdapter("app:musicStatus")
fun setIsPlaying(image: ImageView, musicStatus: Status) {
  val imageResource: Int? = when (musicStatus) {
    Status.PLAYING -> R.drawable.ic_pause
    Status.PAUSED, Status.NO_LOADED, Status.RECORDED -> R.drawable.ic_play
    else -> null
  }
  if (imageResource == null) {
    image.visibility = View.GONE
  } else {
    image.visibility = View.VISIBLE
    image.setImageResource(imageResource)
  }
}

@BindingAdapter("app:levelRating")
fun setLevelRating(ratingBar: RatingBar, level: LevelOfDifficulty?) {
  if (level == null) return

  val rating = when(level) {
    LevelOfDifficulty.EASY -> 1f
    LevelOfDifficulty.MEDIUM -> 2f
    LevelOfDifficulty.ADVANCED -> 3f
  }
  ratingBar.rating = rating
}