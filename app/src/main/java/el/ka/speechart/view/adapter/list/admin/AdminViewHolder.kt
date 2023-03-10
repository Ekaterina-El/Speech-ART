package el.ka.speechart.view.adapter.list.admin

import android.content.Context
import android.view.Menu
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.R
import el.ka.speechart.databinding.ItemAdminBinding
import el.ka.speechart.service.model.User

class AdminViewHolder(val context: Context, val binding: ItemAdminBinding, listener: Listener): RecyclerView.ViewHolder(binding.root) {
  private var user: User? = null

  fun bind(user: User) {
    this.user = user
    binding.user = user

    binding.wrapper.setOnLongClickListener {
      menu.show()
      return@setOnLongClickListener true
    }
  }

  private val menu by lazy {
    val menu = PopupMenu(context, binding.wrapper)
    menu.menu.add(1, DELETE, Menu.NONE, context.getString(R.string.delete))

    menu.setOnMenuItemClickListener {
      when (it.itemId) {
        DELETE -> listener.delete(user!!)
      }

      return@setOnMenuItemClickListener true
    }
    menu
  }

  companion object {
    const val DELETE = 1

    interface Listener {
      fun delete(user: User)
    }
  }

}