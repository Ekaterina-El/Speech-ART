package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import el.ka.speechart.R
import el.ka.speechart.databinding.SpecialistMainFragmentBinding
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.UserViewModel

class SpecialistMainFragment : UserBaseFragment() {
  private lateinit var binding: SpecialistMainFragmentBinding

  private val specialistViewModel by activityViewModels<SpecialistViewModel>()
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController =
      (childFragmentManager.findFragmentById(R.id.fragmentContainerViewSpecialist) as NavHostFragment).navController
    binding.bottomNavigationView.setupWithNavController(navController)
  }

  override fun onResume() {
    super.onResume()
    userViewModel.error.observe(viewLifecycleOwner, errorObserver)
    specialistViewModel.error.observe(viewLifecycleOwner, errorObserver)
  }

  override fun onStop() {
    super.onStop()
    userViewModel.error.removeObserver(errorObserver)
    specialistViewModel.error.removeObserver(errorObserver)
  }

}