package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import el.ka.speechart.databinding.SpecialistProfileFragmentBinding
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.UserViewModel

class SpecialistProfileFragment : UserBaseFragment() {
  private lateinit var binding: SpecialistProfileFragmentBinding

  private val specialistViewModel: SpecialistViewModel by activityViewModels()
  override val userViewModel: UserViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SpecialistProfileFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@SpecialistProfileFragment
      viewModel = this@SpecialistProfileFragment.specialistViewModel
    }
    return binding.root
  }
}