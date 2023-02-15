package el.ka.speechart.view.adapter.list.performedExercises

import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemPerformedExerciseBinding
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.model.PerformedExercise

class PerformedExerciseViewHolder(val binding: ItemPerformedExerciseBinding): RecyclerView.ViewHolder(binding.root) {
  fun bind(exercise: PerformedExercise, onSelectItem: (PerformedExercise) -> Unit) {
    binding.exercise = exercise
    binding.wrapper.setOnClickListener { onSelectItem(exercise) }
  }


}