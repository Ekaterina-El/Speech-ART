package el.ka.speechart.view.adapter.list.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemAdminBinding
import el.ka.speechart.service.model.User
import el.ka.speechart.view.adapter.list.BaseAdapter

class AdminAdapter: BaseAdapter<User>() {
  override val items = mutableListOf<User>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemAdminBinding.inflate(layoutInflater, parent, false)
    return AdminViewHolder(binding)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val user = items[position]
    (holder as AdminViewHolder).bind(user)
  }
}