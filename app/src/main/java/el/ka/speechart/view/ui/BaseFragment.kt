package el.ka.speechart.view.ui

import android.app.Dialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import el.ka.speechart.MainActivity
import el.ka.speechart.R
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.Work
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

  private val errorDialog by lazy { ErrorDialog(requireContext()) }

  private fun showErrorDialog(error: ErrorApp) {
    val message = getString(error.messageRes)
    errorDialog.openConfirmDialog(message)
  }
}