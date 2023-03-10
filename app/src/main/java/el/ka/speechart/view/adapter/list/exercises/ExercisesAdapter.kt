package el.ka.speechart.view.adapter.list.exercises

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemExerciseBinding
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.view.adapter.list.BaseAdapter

class ExercisesAdapter(
  private val context: Context,
  private val listener: ExerciseViewHolder.Companion.Listener? = null,
  private val onItemSelected: (Exercise) -> Unit
) : BaseAdapter<Exercise>() {
  override val items = mutableListOf<Exercise>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemExerciseBinding.inflate(layoutInflater, parent, false)
    return ExerciseViewHolder(context, binding, listener)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    val exerciseViewHolder = (holder as ExerciseViewHolder)
    exerciseViewHolder.bind(item, onItemSelected)
  }
}