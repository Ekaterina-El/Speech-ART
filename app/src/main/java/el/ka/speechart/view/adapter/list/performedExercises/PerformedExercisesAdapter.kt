package el.ka.speechart.view.adapter.list.performedExercises

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemExerciseBinding
import el.ka.speechart.databinding.ItemPerformedExerciseBinding
import el.ka.speechart.service.model.PerformedExercise
import el.ka.speechart.view.adapter.list.BaseAdapter
import el.ka.speechart.view.adapter.list.exercises.ExerciseViewHolder

class PerformedExercisesAdapter(private val onSelectItem: (PerformedExercise) -> Unit) : BaseAdapter<PerformedExercise>() {
  override val items = mutableListOf<PerformedExercise>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformedExerciseViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemPerformedExerciseBinding.inflate(layoutInflater, parent, false)
    return PerformedExerciseViewHolder(binding)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    val exerciseViewHolder = (holder as PerformedExerciseViewHolder)
    exerciseViewHolder.bind(item, onSelectItem)
  }
}