package el.ka.speechart.view.adapter.list.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemAdminBinding
import el.ka.speechart.service.model.User
import el.ka.speechart.view.adapter.list.BaseAdapter

class AdminAdapter(private val context: Context, private val listener: AdminViewHolder.Companion.Listener): BaseAdapter<User>() {
  override val items = mutableListOf<User>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemAdminBinding.inflate(layoutInflater, parent, false)
    return AdminViewHolder(context, binding, listener)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val user = items[position]
    (holder as AdminViewHolder).bind(user)
  }
}