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
import el.ka.speechart.databinding.SpecialistListOfRequestsToCheckFragmentBinding
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.PerformedExercise
import el.ka.speechart.view.adapter.list.performedExercises.PerformedExercisesAdapter
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.UserViewModel

class SpecialistRequestsToCheckFragment(val openItem: (PerformedExercise) -> Unit) :
  UserBaseFragment() {
  private lateinit var binding: SpecialistListOfRequestsToCheckFragmentBinding
  private lateinit var performedExercisesAdapter: PerformedExercisesAdapter

  private val exerciseViewModel by activityViewModels<ExerciseViewModel>()

  private val performedExercisesObserver = Observer<List<PerformedExercise>> {
    performedExercisesAdapter.setItems(it)
  }

  val list = listOf(Work.LOAD_PERFORMED_EXERCISE)
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

  override val userViewModel: UserViewModel by activityViewModels()
  private val specialistViewModel: SpecialistViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SpecialistListOfRequestsToCheckFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )
    performedExercisesAdapter = PerformedExercisesAdapter { performedExercise ->
      exerciseViewModel.setPerformedExercise(performedExercise)
      openItem(performedExercise)
    }
    specialistViewModel.setContext(requireContext())

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@SpecialistRequestsToCheckFragment.specialistViewModel
      performedExercisesAdapter = this@SpecialistRequestsToCheckFragment.performedExercisesAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (specialistViewModel.countOfLoadedPerformedExercise == 0) specialistViewModel.loadPerformedExercises()

    val color = requireContext().getColor(R.color.loader_color)
    binding.swipeRefreshLayout.setColorSchemeColors(color)
    binding.swipeRefreshLayout.setOnRefreshListener { specialistViewModel.loadPerformedExercises() }

    binding.swipeRefreshLayout2.setColorSchemeColors(color)
    binding.swipeRefreshLayout2.setOnRefreshListener { specialistViewModel.loadPerformedExercises() }

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.recyclerViewExercises.addItemDecoration(decorator)

    binding.noFound.findViewById<TextView>(R.id.message).text =
      getString(R.string.exercises_no_found)
  }

  override fun onResume() {
    super.onResume()
    specialistViewModel.filteredPerformedExercise.observe(
      viewLifecycleOwner,
      performedExercisesObserver
    )
    specialistViewModel.work.observe(viewLifecycleOwner, workObserver)
  }

  override fun onStop() {
    super.onStop()
    specialistViewModel.filteredPerformedExercise.removeObserver(performedExercisesObserver)
    specialistViewModel.work.removeObserver(workObserver)
  }
}