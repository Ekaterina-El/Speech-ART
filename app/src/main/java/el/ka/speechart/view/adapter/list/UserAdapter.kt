package el.ka.speechart.view.adapter.list

import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.service.model.User

abstract class UserAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
  protected open val items = mutableListOf<User>()

  override fun getItemCount() = items.size

  fun setItems(newItems: List<User>) {
    if (items == newItems) return
    clear()
    newItems.forEach { setItem(it) }
  }

  fun addItem(user: User, position: Int? = null) {
    val pos = if (position == null || position < 0) items.size else position
    items.add(pos, user)
    notifyItemInserted(pos)
  }

  fun removeItem(user: User) {
    val idx = items.indexOf(user)
    if (idx == -1) return

    items.removeAt(idx)
    notifyItemRemoved(idx)
  }

  private fun setItem(item: User) {
    items.add(item)
    notifyItemInserted(items.size)
  }

  private fun clear() {
    notifyItemRangeRemoved(0, items.size)
    items.clear()
  }

}