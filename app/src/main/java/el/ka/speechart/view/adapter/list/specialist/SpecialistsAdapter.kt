package el.ka.speechart.view.adapter.list.specialist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemSpecialistBinding
import el.ka.speechart.service.model.User
import el.ka.speechart.view.adapter.list.BaseAdapter

class SpecialistsAdapter(val context: Context, val listener: SpecialistViewHolder.Companion.Listener): BaseAdapter<User>() {
  override val items = mutableListOf<User>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialistViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemSpecialistBinding.inflate(layoutInflater, parent, false)
    return SpecialistViewHolder(context, binding, listener)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val user = items[position]
    (holder as SpecialistViewHolder).bind(user)
  }
}