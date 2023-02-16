package el.ka.speechart.view.adapter.list.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemReviewBinding
import el.ka.speechart.service.model.Review
import el.ka.speechart.view.adapter.list.BaseAdapter

class ReviewsAdapter : BaseAdapter<Review>() {
  override val items = mutableListOf<Review>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemReviewBinding.inflate(layoutInflater, parent, false)
    return ReviewsViewHolder(binding)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    val exerciseViewHolder = (holder as ReviewsViewHolder)
    exerciseViewHolder.bind(item)
  }
}