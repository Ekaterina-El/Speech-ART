package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import el.ka.speechart.databinding.AdminMainFragmentBinding
import el.ka.speechart.databinding.SpecialistMainFragmentBinding
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.UserViewModel

class SpecialistMainFragment: UserBaseFragment() {
  private lateinit var binding: SpecialistMainFragmentBinding
  override val userViewModel by activityViewModels<UserViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SpecialistMainFragmentBinding.inflate(layoutInflater)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

}