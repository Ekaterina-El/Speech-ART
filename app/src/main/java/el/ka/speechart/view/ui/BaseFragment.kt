package el.ka.speechart.view.ui

import android.app.Dialog
import android.content.Intent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import el.ka.speechart.MainActivity
import el.ka.speechart.R
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Work
import el.ka.speechart.view.dialog.ConfirmDialog
import el.ka.speechart.view.dialog.ErrorDialog

open class BaseFragment : Fragment() {
  val workObserver = Observer<List<Work>> {
    if (it.isEmpty()) hideLoadingDialog() else showLoadingDialog()
  }

  val errorObserver = Observer<ErrorApp?> {
    if (it != null) showErrorDialog(it)
  }

  private fun getLoadingDialog(): Dialog {
    val activity = requireActivity() as MainActivity
    if (activity.loadingDialog == null) createLoadingDialog()
    return activity.loadingDialog!!
  }

  private fun showLoadingDialog() {
    val dialog = getLoadingDialog()
    if (!dialog.isShowing) dialog.show()
  }

  private fun hideLoadingDialog() {
    val dialog = getLoadingDialog()
    dialog.dismiss()
  }

  private fun createLoadingDialog() {
    val loadingDialog = Dialog(requireContext(), R.style.AppTheme_FullScreenDialog)
    loadingDialog.setContentView(R.layout.fragment_loading_progress_bar)
    loadingDialog.window!!.setLayout(
      ConstraintLayout.LayoutParams.MATCH_PARENT,
      ConstraintLayout.LayoutParams.MATCH_PARENT,
    )
//    loadingDialog.window!!.setWindowAnimations(R.style.Slide)
    loadingDialog.setCancelable(false)

    val activity = requireActivity() as MainActivity
    activity.loadingDialog = loadingDialog
  }

  // region Error dialog
  private val errorDialog by lazy { ErrorDialog(requireContext()) }

  private fun showErrorDialog(error: ErrorApp) {
    val message = getString(error.messageRes)
    errorDialog.openConfirmDialog(message)
  }
  // endregion

  // region Confirm Dialog
  private var confirmDialog: ConfirmDialog? = null

  fun openConfirmDialog(
    message: String,
    confirmListener: ConfirmDialog.Companion.ConfirmListener,
    value: Any? = null
  ) {
    if (confirmDialog == null) confirmDialog = ConfirmDialog.getInstance(requireContext())
    confirmDialog!!.openConfirmDialog(message, confirmListener, value)
  }

  fun closeConfirmDialog() {
    confirmDialog?.closeConfirmDialog()
  }
  // endregion

  fun restartApp() {
    val i = requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
      ?: return
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(i)
    ActivityCompat.finishAfterTransition(requireActivity())
  }
}