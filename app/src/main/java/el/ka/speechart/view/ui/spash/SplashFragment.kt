package el.ka.speechart.view.ui.spash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import el.ka.speechart.R
import el.ka.speechart.databinding.SplashFragmentBinding
import el.ka.speechart.other.Constants
import el.ka.speechart.other.actionFromSplash
import el.ka.speechart.service.model.User
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.UserViewModel

class SplashFragment : BaseFragment() {
  private lateinit var binding: SplashFragmentBinding
  private val userViewModel by activityViewModels<UserViewModel>()

  private val userObserver = Observer<User?> {
    if (userViewModel.userLoaded) {
      val action = when (it) {
        null -> R.id.action_splashFragment_to_welcomeFragment
        else -> it.role.actionFromSplash
      }
      findNavController().navigate(action)
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    requireActivity().window.statusBarColor = requireContext().getColor(R.color.secondary_color)

    binding = SplashFragmentBinding.inflate(layoutInflater)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }

    return binding.root
  }

  override fun onResume() {
    super.onResume()
    Handler(Looper.getMainLooper()).postDelayed({
      loadCurrentUser()
    }, Constants.LOAD_DELAY)

    userViewModel.user.observe(viewLifecycleOwner, userObserver)
  }

  override fun onStop() {
    super.onStop()
    userViewModel.user.removeObserver(userObserver)
  }

  private fun loadCurrentUser() {
    userViewModel.loadCurrentUser()
  }
}