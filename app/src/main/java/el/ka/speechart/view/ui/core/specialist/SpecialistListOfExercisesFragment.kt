package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import el.ka.speechart.databinding.SpecialistListOfExercisesFragmentBinding
import el.ka.speechart.view.ui.BaseFragment

class SpecialistListOfExercisesFragment : BaseFragment() {
  private lateinit var binding: SpecialistListOfExercisesFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SpecialistListOfExercisesFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }
}