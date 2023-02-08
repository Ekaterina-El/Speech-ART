package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import el.ka.speechart.R
import el.ka.speechart.databinding.SpecialistListOfExercisesFragmentBinding
import el.ka.speechart.view.adapter.list.exercises.ExercisesAdapter
import el.ka.speechart.view.adapter.list.specialist.SpecialistsAdapter
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.UserViewModel
import org.w3c.dom.Text

class SpecialistListOfExercisesFragment : UserBaseFragment() {
  private lateinit var binding: SpecialistListOfExercisesFragmentBinding
  override val userViewModel: UserViewModel by activityViewModels()
  private val specialistViewModel: SpecialistViewModel by activityViewModels()

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

    binding.noFound.findViewById<TextView>(R.id.message).setText(getString(R.string.exercises_no_found))
  }

  fun showDialogForAddExercises() {

  }
}