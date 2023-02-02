package el.ka.speechart.view.ui.core.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import el.ka.speechart.databinding.AdminMainFragmentBinding
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.UserViewModel

class AdminMainFragment: BaseFragment() {
  private lateinit var binding: AdminMainFragmentBinding
  private val userViewModel by activityViewModels<UserViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = AdminMainFragmentBinding.inflate(layoutInflater)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

}