package el.ka.speechart.view.ui

import androidx.lifecycle.Observer
import el.ka.speechart.R
import el.ka.speechart.other.Action
import el.ka.speechart.view.dialog.ConfirmDialog
import el.ka.speechart.viewModel.UserViewModel

abstract class UserBaseFragment : BaseFragment() {
  protected abstract val userViewModel: UserViewModel

  protected open val externalActionObserver = Observer<Action?> {
    if (it == Action.RESTART) {
      setCredentials(null)
      restartApp()
    }
  }

  private val exitDialogListener by lazy {
    object : ConfirmDialog.Companion.ConfirmListener {
      override fun onAgree(value: Any?) {
        userViewModel.logout()
        closeConfirmDialog()
      }

      override fun onDisagree() {
        closeConfirmDialog()
      }

    }
  }

  fun showExitDialog() {
    openConfirmDialog(getString(R.string.exit_message), exitDialogListener)
  }

  override fun onStart() {
    super.onStart()
    userViewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
  }

  override fun onStop() {
    super.onStop()
    userViewModel.externalAction.removeObserver(externalActionObserver)
  }
}