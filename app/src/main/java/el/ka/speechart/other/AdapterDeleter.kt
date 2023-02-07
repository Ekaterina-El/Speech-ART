package el.ka.speechart.other

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class AdapterDeleter(
  val onLeft: (RecyclerView.ViewHolder) -> Unit,
  val onRight: ((RecyclerView.ViewHolder) -> Unit) = onLeft,
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder
  ) = false

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    when (direction) {
      ItemTouchHelper.LEFT -> onLeft(viewHolder)
      ItemTouchHelper.RIGHT -> onRight(viewHolder)
    }
  }

  override fun onChildDrawOver(
    c: Canvas,
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder?,
    dX: Float,
    dY: Float,
    actionState: Int,
    isCurrentlyActive: Boolean
  ) {
    Log.d("onChildDrawOver", "$dX : $dY")

    super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
  }
}