package el.ka.speechart.view.ui.core.specialist

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
import el.ka.speechart.other.LevelOfDifficulty
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.view.adapter.list.exercises.ExercisesAdapter
import el.ka.speechart.view.dialog.AddExerciseDialog
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.UserViewModel

class SpecialistListOfExercisesFragment : UserBaseFragment() {
  private lateinit var binding: SpecialistListOfExercisesFragmentBinding
  override val userViewModel: UserViewModel by activityViewModels()
  private val specialistViewModel: SpecialistViewModel by activityViewModels()

  val list = listOf(Work.LOAD_EXERCISES, Work.ADD_EXERCISE)
  override val workObserver = Observer<List<Work>> {
    val isLoad =
      when {
        it.isEmpty() -> false
        else -> it.map { item -> if (list.contains(item)) 1 else 0 }.reduce { a, b -> a + b } > 0
      }
    binding.swipeRefreshLayout.isRefreshing = isLoad
    binding.swipeRefreshLayout2.isRefreshing = isLoad
  }

  private val exercisesObserver = Observer<List<Exercise>> {
    exercisesAdapter.setItems(it)
  }

  private val exercisesAdapter by lazy { ExercisesAdapter() }

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

    if (specialistViewModel.countOfExercises == 0) specialistViewModel.loadExercises()

    val color = requireContext().getColor(R.color.loader_color)
    binding.swipeRefreshLayout.setColorSchemeColors(color)
    binding.swipeRefreshLayout.setOnRefreshListener { specialistViewModel.loadExercises() }

    binding.swipeRefreshLayout2.setColorSchemeColors(color)
    binding.swipeRefreshLayout2.setOnRefreshListener { specialistViewModel.loadExercises() }

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewExercises.addItemDecoration(decorator)

    binding.noFound.findViewById<TextView>(R.id.message).text = getString(R.string.exercises_no_found)
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

  private val addExerciseDialogListener by lazy {
    object: AddExerciseDialog.Companion.Listener {
      override fun onSave(exercise: Exercise) {
        specialistViewModel.addExercise(exercise)
      }
    }
  }

  private val addExerciseDialog by lazy {
    AddExerciseDialog(requireContext(), this, addExerciseDialogListener)
  }
  fun showDialogForAddExercises() {
    addExerciseDialog.open()
  }
}