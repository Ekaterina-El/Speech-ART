package el.ka.speechart.view.adapter.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import el.ka.speechart.other.ImageLoader

@BindingAdapter("app:imageUrl")
fun setImageByUrl(image: ImageView, url: String?) {
  ImageLoader.getInstance(image.context).loadTo(url ?: "", image)
}