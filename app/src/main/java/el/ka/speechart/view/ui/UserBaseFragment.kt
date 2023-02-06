package el.ka.speechart.view.ui

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import el.ka.speechart.MainActivity
import el.ka.speechart.R
import el.ka.speechart.other.Constants
import el.ka.speechart.other.Credentials
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Work
import el.ka.speechart.view.dialog.ConfirmDialog
import el.ka.speechart.view.dialog.InformDialog
import el.ka.speechart.viewModel.UserViewModel

abstract class UserBaseFragment : BaseFragment() {
  protected abstract val userViewModel: UserViewModel

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
}