package el.ka.speechart.view.adapter.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import el.ka.speechart.R
import el.ka.speechart.other.ImageLoader

@BindingAdapter("app:imageUrl")
fun setImageByUrl(image: ImageView, url: String?) {
  ImageLoader.getInstance(image.context).loadTo(url ?: "", image)
}

@BindingAdapter("app:isPlaying")
fun setIsPlaying(image: ImageView, isPlaying: Boolean) {
  val imageResource = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
  image.setImageResource(imageResource)
}