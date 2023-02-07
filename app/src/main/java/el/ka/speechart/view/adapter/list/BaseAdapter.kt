package el.ka.speechart.view.adapter.list

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
  protected open val items = mutableListOf<T>()

  override fun getItemCount() = items.size

  fun setItems(newItems: List<T>) {
    if (items == newItems) return
    clear()
    newItems.forEach { setItem(it) }
  }

  fun addItem(user: T, position: Int? = null) {
    val pos = if (position == null || position < 0) items.size else position
    items.add(pos, user)
    notifyItemInserted(pos)
  }

  fun removeItem(user: T) {
    val idx = items.indexOf(user)
    if (idx == -1) return

    items.removeAt(idx)
    notifyItemRemoved(idx)
  }

  private fun setItem(item: T) {
    items.add(item)
    notifyItemInserted(items.size)
  }

  private fun clear() {
    notifyItemRangeRemoved(0, items.size)
    items.clear()
  }

}