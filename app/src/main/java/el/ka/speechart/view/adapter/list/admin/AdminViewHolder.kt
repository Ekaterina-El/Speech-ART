package el.ka.speechart.view.adapter.list.admin

import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemAdminBinding
import el.ka.speechart.service.model.User

class AdminViewHolder(val binding: ItemAdminBinding): RecyclerView.ViewHolder(binding.root) {
  fun bind(user: User) {
    binding.user = user
  }


}