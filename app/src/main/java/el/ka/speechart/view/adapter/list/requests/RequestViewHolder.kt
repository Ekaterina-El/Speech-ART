package el.ka.speechart.view.adapter.list.requests

import android.content.Context
import android.view.Menu
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.R
import el.ka.speechart.databinding.ItemRequestsBinding
import el.ka.speechart.service.model.RequestToRegSpecialist

class RequestViewHolder(val context: Context, val binding: ItemRequestsBinding, val listener: Listener): RecyclerView.ViewHolder(binding.root) {
  private val menu by lazy {
    val menu = PopupMenu(context, binding.wrapper)
    menu.menu.add(1, AGREE_REQUEST, Menu.NONE, context.getString(R.string.agree))
    menu.menu.add(1, DISAGREE_REQUEST, Menu.NONE, context.getString(R.string.disagree))

    menu.setOnMenuItemClickListener {
      when (it.itemId) {
        AGREE_REQUEST -> listener.agree(request!!)
        DISAGREE_REQUEST -> listener.disagree(request!!)
      }

      return@setOnMenuItemClickListener true
    }

    menu
  }

  private var request: RequestToRegSpecialist? = null

  fun bind(request: RequestToRegSpecialist) {
    binding.request = request
    this.request = request

    binding.wrapper.setOnClickListener {
      menu.show()
    }
  }

  companion object {
    const val AGREE_REQUEST = 1
    const val DISAGREE_REQUEST = 2

    interface Listener {
      fun agree(request: RequestToRegSpecialist)
      fun disagree(request: RequestToRegSpecialist)
    }
  }
}