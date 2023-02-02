package el.ka.speechart.view.ui.core.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.speechart.databinding.AdminMainFragmentBinding
import el.ka.speechart.databinding.OwnerMainFragmentBinding
import el.ka.speechart.service.model.User
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.AdminViewModel
import el.ka.speechart.viewModel.UserViewModel

class OwnerMainFragment: BaseFragment() {
  private lateinit var binding: OwnerMainFragmentBinding
  private val userViewModel by activityViewModels<UserViewModel>()
  private val adminViewModel by activityViewModels<AdminViewModel>()

  private val adminsObserver = Observer<List<User>> {
    val admins = it
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = OwnerMainFragmentBinding.inflate(layoutInflater)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adminViewModel.loadAdmins()
  }

  override fun onResume() {
    super.onResume()
    adminViewModel.admins.observe(viewLifecycleOwner, adminsObserver)
    adminViewModel.error.observe(viewLifecycleOwner, errorObserver)
    adminViewModel.work.observe(viewLifecycleOwner, workObserver)
  }

  override fun onStop() {
    super.onStop()
    adminViewModel.admins.removeObserver(adminsObserver)
    adminViewModel.error.removeObserver(errorObserver)
    adminViewModel.work.removeObserver(workObserver)
  }

}