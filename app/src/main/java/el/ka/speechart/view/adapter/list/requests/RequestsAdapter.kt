package el.ka.speechart.view.adapter.list.requests

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemRequestsBinding
import el.ka.speechart.databinding.ItemSpecialistBinding
import el.ka.speechart.service.model.RequestToRegSpecialist
import el.ka.speechart.view.adapter.list.BaseAdapter

class RequestToRegSpecialistAdapter(private val context: Context, val listener: RequestViewHolder.Companion.Listener) : BaseAdapter<RequestToRegSpecialist>() {
  override val items = mutableListOf<RequestToRegSpecialist>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemRequestsBinding.inflate(layoutInflater, parent, false)
    return RequestViewHolder(context, binding, listener)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    (holder as RequestViewHolder).bind(item)
  }
}