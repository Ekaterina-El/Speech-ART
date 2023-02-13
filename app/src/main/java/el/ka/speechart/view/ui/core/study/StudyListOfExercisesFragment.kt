package el.ka.speechart.view.ui.core.study

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import el.ka.speechart.R
import el.ka.speechart.databinding.SpecialistListOfExercisesFragmentBinding
import el.ka.speechart.databinding.StudyListOfExercisesFragmentBinding
import el.ka.speechart.other.Constants
import el.ka.speechart.other.FilePicker
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.view.adapter.list.exercises.ExercisesAdapter
import el.ka.speechart.view.dialog.AddExerciseDialog
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.StudyViewModel
import el.ka.speechart.viewModel.UserViewModel

class StudyListOfExercisesFragment : UserBaseFragment() {
  private lateinit var binding: StudyListOfExercisesFragmentBinding
  override val userViewModel: UserViewModel by activityViewModels()
  private val studyViewModel: StudyViewModel by activityViewModels()

  val list = listOf(Work.LOAD_EXERCISES)
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
    ExercisesAdapter {
      /*val destination = SpecialistListOfExercisesFragmentDirections
        .actionSpecialistListOfExercisesFragmentToSpecialistExerciseFragment(it)
      findNavController().navigate(destination)*/
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    studyViewModel.setContext(requireContext())
    binding = StudyListOfExercisesFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@StudyListOfExercisesFragment
      viewModel = this@StudyListOfExercisesFragment.studyViewModel
      exercisesAdapter = this@StudyListOfExercisesFragment.exercisesAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (studyViewModel.countOfLoadedExercise == 0) studyViewModel.loadExercises()

    val color = requireContext().getColor(R.color.loader_color)
    binding.swipeRefreshLayout.setColorSchemeColors(color)
    binding.swipeRefreshLayout.setOnRefreshListener { studyViewModel.loadExercises() }

    binding.swipeRefreshLayout2.setColorSchemeColors(color)
    binding.swipeRefreshLayout2.setOnRefreshListener {studyViewModel.loadExercises() }

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewExercises.addItemDecoration(decorator)

    binding.noFound.findViewById<TextView>(R.id.message).text =
      getString(R.string.exercises_no_found)
  }

  override fun onResume() {
    super.onResume()
    studyViewModel.filteredExercises.observe(viewLifecycleOwner, exercisesObserver)
    studyViewModel.work.observe(viewLifecycleOwner, workObserver)
  }

  override fun onStop() {
    super.onStop()
    studyViewModel.filteredExercises.removeObserver(exercisesObserver)
    studyViewModel.work.removeObserver(workObserver)
  }
}