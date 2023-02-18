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
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Errors
import el.ka.speechart.other.actionFromSplash
import el.ka.speechart.service.model.User
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.UserViewModel

class SplashFragment : UserBaseFragment() {
  private lateinit var binding: SplashFragmentBinding
  override val userViewModel by activityViewModels<UserViewModel>()

  override val errorObserver = Observer<ErrorApp?> {
    if (it == null) return@Observer

    if (it == Errors.documentNoFound) {
      val title = getString(R.string.user_document_not_found)
      val message = getString(R.string.user_document_not_found_message)
      showInformDialog(title, message, onClickOk = {
        userViewModel.logout()
      })
    } else {
      showErrorDialog(it)
    }
  }

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
    userViewModel.error.observe(viewLifecycleOwner, errorObserver)
  }

  override fun onStop() {
    super.onStop()
    userViewModel.user.removeObserver(userObserver)
    userViewModel.error.removeObserver(errorObserver)
  }

  private fun loadCurrentUser() {
    userViewModel.loadCurrentUser()
  }
}