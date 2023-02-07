package el.ka.speechart.other

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min


class AdapterDeleter(
  val onLeft: (RecyclerView.ViewHolder) -> Unit,
  val onRight: ((RecyclerView.ViewHolder) -> Unit) = onLeft,
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
  private val paint: Paint = Paint()

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
}