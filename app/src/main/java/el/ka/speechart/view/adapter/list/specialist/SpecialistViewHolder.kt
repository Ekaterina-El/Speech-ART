package el.ka.speechart.view.adapter.list.specialist

import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemSpecialistBinding
import el.ka.speechart.service.model.User

class SpecialistViewHolder(val binding: ItemSpecialistBinding): RecyclerView.ViewHolder(binding.root) {
  fun bind(user: User) {
    binding.user = user
  }


}