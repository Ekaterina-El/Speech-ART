package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import el.ka.speechart.databinding.SpecialistExerciseFragmentBinding
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel

class SpecialistExerciseFragment : BaseFragment() {
  private lateinit var binding: SpecialistExerciseFragmentBinding
  private lateinit var exerciseViewModel: ExerciseViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    exerciseViewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]
    binding = SpecialistExerciseFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    requireActivity().onBackPressedDispatcher.addCallback(this) {
      findNavController().popBackStack()
    }

    binding.apply {
      master = this@SpecialistExerciseFragment
      viewModel = this@SpecialistExerciseFragment.exerciseViewModel
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val exercise = SpecialistExerciseFragmentArgs.fromBundle(requireArguments()).exercise
    exerciseViewModel.setExercise(exercise)
  }
}