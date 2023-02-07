package el.ka.speechart.view.adapter.list.requests

import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemRequestsBinding
import el.ka.speechart.service.model.RequestToRegSpecialist

class RequestViewHolder(val binding: ItemRequestsBinding): RecyclerView.ViewHolder(binding.root) {
  fun bind(request: RequestToRegSpecialist) {
    binding.request = request
  }
}