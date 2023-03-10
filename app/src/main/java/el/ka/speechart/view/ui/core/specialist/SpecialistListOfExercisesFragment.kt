package el.ka.speechart.view.ui.core.specialist

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import el.ka.speechart.R
import el.ka.speechart.databinding.SpecialistListOfExercisesFragmentBinding
import el.ka.speechart.other.Constants
import el.ka.speechart.other.FilePicker
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.view.adapter.list.exercises.ExerciseViewHolder
import el.ka.speechart.view.adapter.list.exercises.ExercisesAdapter
import el.ka.speechart.view.dialog.ConfirmDialog
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.UserViewModel

class SpecialistListOfExercisesFragment(onItemSelected: () -> Unit, val openAddExerciseFragment: () -> Unit) : UserBaseFragment() {
  private lateinit var binding: SpecialistListOfExercisesFragmentBinding

  override val userViewModel: UserViewModel by activityViewModels()
  private val exerciseViewModel: ExerciseViewModel by activityViewModels()
  private val specialistViewModel: SpecialistViewModel by activityViewModels()

  private val exerciseAdapterListener = object : ExerciseViewHolder.Companion.Listener {
    override fun delete(exercise: Exercise) {
      val message = getString(R.string.delete_exercise_message, exercise.name)
      openConfirmDialog(message, object : ConfirmDialog.Companion.ConfirmListener {
        override fun onAgree(value: Any?) {
          specialistViewModel.deleteExercise(exercise)
          closeConfirmDialog()
        }

        override fun onDisagree() {
          closeConfirmDialog()
        }
      })
    }
  }

  val list = listOf(Work.LOAD_EXERCISES, Work.ADD_EXERCISE, Work.DELETE_EXERCISE)
  private val List<Work>.hasLoad: Boolean
    get() =
      when {
        this.isEmpty() -> false
        else -> this.map { item -> if (list.contains(item)) 1 else 0 }.reduce { a, b -> a + b } > 0
      }

  override val workObserver = Observer<List<Work>> {
    binding.swipeRefreshLayout.isRefreshing = it.hasLoad
    binding.swipeRefreshLayout2.isRefreshing = it.hasLoad
  }

  private val exercisesObserver = Observer<List<Exercise>> {
    exercisesAdapter.setItems(it)
  }

  private val exercisesAdapter by lazy {
    ExercisesAdapter(requireContext(), exerciseAdapterListener) { exercise ->
      exerciseViewModel.setExercise(exercise)
      onItemSelected()
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    specialistViewModel.setContext(requireContext())
    binding = SpecialistListOfExercisesFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@SpecialistListOfExercisesFragment
      viewModel = this@SpecialistListOfExercisesFragment.specialistViewModel
      exercisesAdapter = this@SpecialistListOfExercisesFragment.exercisesAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (specialistViewModel.countOfLoadedExercise == 0) specialistViewModel.loadExercises()

    val color = requireContext().getColor(R.color.loader_color)
    binding.swipeRefreshLayout.setColorSchemeColors(color)
    binding.swipeRefreshLayout.setOnRefreshListener { specialistViewModel.loadExercises() }

    binding.swipeRefreshLayout2.setColorSchemeColors(color)
    binding.swipeRefreshLayout2.setOnRefreshListener { specialistViewModel.loadExercises() }

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewExercises.addItemDecoration(decorator)

    binding.noFound.findViewById<TextView>(R.id.message).text =
      getString(R.string.exercises_no_found)
  }

  override fun onResume() {
    super.onResume()
    specialistViewModel.filteredExercises.observe(viewLifecycleOwner, exercisesObserver)
    specialistViewModel.work.observe(viewLifecycleOwner, workObserver)
  }

  override fun onStop() {
    super.onStop()
    specialistViewModel.filteredExercises.removeObserver(exercisesObserver)
    specialistViewModel.work.removeObserver(workObserver)
  }
  fun addExercise() {
    if (userViewModel.work.value!!.hasLoad || specialistViewModel.work.value!!.hasLoad) return
    openAddExerciseFragment()
  }
}