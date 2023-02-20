package el.ka.speechart.view.adapter.list.exercises

import android.content.Context
import android.view.Menu
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.R
import el.ka.speechart.databinding.ItemExerciseBinding
import el.ka.speechart.service.model.Exercise

class ExerciseViewHolder(
  val context: Context,
  val binding: ItemExerciseBinding,
  val listener: Listener?
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(exercise: Exercise, onItemSelected: (Exercise) -> Unit) {
    this.exercise = exercise
    binding.exercise = exercise
    binding.wrapper.setOnClickListener { onItemSelected(exercise)  }
    if (listener != null) binding.wrapper.setOnLongClickListener {
      menu.show()
      return@setOnLongClickListener true
    }
  }

  private var exercise: Exercise? = null


  private val menu by lazy {
    val menu = PopupMenu(context, binding.wrapper)
    menu.menu.add(1, DELETE, Menu.NONE, context.getString(R.string.delete))

    menu.setOnMenuItemClickListener {
      when (it.itemId) {
        DELETE -> listener?.delete(exercise!!)
      }

      return@setOnMenuItemClickListener true
    }

    menu
  }

  companion object {
    const val DELETE = 1

    interface Listener {
      fun delete(exercise: Exercise)
    }
  }


}