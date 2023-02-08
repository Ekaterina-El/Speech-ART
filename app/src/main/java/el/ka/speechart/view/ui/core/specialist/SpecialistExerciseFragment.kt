package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import el.ka.speechart.databinding.SpecialistExerciseFragmentBinding
import el.ka.speechart.view.ui.BaseFragment

class SpecialistExerciseFragment : BaseFragment() {
  private lateinit var binding: SpecialistExerciseFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SpecialistExerciseFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }
}