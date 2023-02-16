package el.ka.speechart.view.adapter.list.reviews

import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemReviewBinding
import el.ka.speechart.service.model.Review

class ReviewsViewHolder(val binding: ItemReviewBinding): RecyclerView.ViewHolder(binding.root) {
  fun bind(review: Review) {
    binding.review = review
  }


}