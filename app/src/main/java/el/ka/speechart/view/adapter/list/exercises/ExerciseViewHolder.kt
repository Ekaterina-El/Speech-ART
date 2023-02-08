package el.ka.speechart.view.adapter.list.exercises

import androidx.recyclerview.widget.RecyclerView
import el.ka.speechart.databinding.ItemExerciseBinding
import el.ka.speechart.service.model.Exercise

class ExerciseViewHolder(val binding: ItemExerciseBinding): RecyclerView.ViewHolder(binding.root) {
  fun bind(exercise: Exercise) {
    binding.exercise = exercise
  }


}