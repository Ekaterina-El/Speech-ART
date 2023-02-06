package el.ka.speechart.view.ui.core.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import el.ka.speechart.R
import el.ka.speechart.databinding.AdminMainFragmentBinding
import el.ka.speechart.other.Work
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.AdminViewModel
import el.ka.speechart.viewModel.UserViewModel

class AdminMainFragment : UserBaseFragment() {
  private lateinit var binding: AdminMainFragmentBinding
  override val userViewModel by activityViewModels<UserViewModel>()
  private val adminViewModel by activityViewModels<AdminViewModel>()

  override val workObserver = Observer<List<Work>> {
    if (it.contains(Work.LOAD_USERS) || it.contains(Work.LOAD_REQUESTS)) {
      if (it.isEmpty()) hideLoadingDialog()
    } else if (it.isEmpty()) hideLoadingDialog()
    else showLoadingDialog()
  }

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController =
      (childFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
        .navController
    binding.bottomNavigationView.setupWithNavController(navController)
  }

  override fun onResume() {
    super.onResume()
    adminViewModel.work.observe(viewLifecycleOwner, workObserver)
    adminViewModel.error.observe(viewLifecycleOwner, errorObserver)
  }

}