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

open class BaseFragment : Fragment() {
  open val workObserver = Observer<List<Work>> {
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

  fun showLoadingDialog() {
    val dialog = getLoadingDialog()
    if (!dialog.isShowing) dialog.show()
  }

  fun hideLoadingDialog() {
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

  // region Inform dialog
  private val informDialog by lazy { InformDialog(requireContext()) }

  private fun showErrorDialog(error: ErrorApp) {
    val title = getString(R.string.error_dialog_title)
    val message = getString(error.messageRes)
    informDialog.open(title, message)
  }

  fun showInformDialog(title: String, message: String, warningText: String? = null, onClickText: () -> Unit = {}) {
    informDialog.open(title, message, warningText, onClickText)
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

  // region Credentials
  private val mainActivity by lazy { requireActivity() as MainActivity }
  fun getCredentials(): Credentials? {
    val credentials = mainActivity.sharedPreferences.getString(Constants.CREDENTIALS, null) ?: return null
    val parts = credentials.split(Constants.SEPARATOR)
    return when (parts.size) {
      2 -> Credentials(parts[0], parts[1])
      else -> null
    }
  }

  fun setCredentials(credentials: Credentials?) {
    val edit = mainActivity.sharedPreferences.edit()
    val s = when (credentials) {
      null -> ""
      else -> "${credentials.email}${Constants.SEPARATOR}${credentials.password}"
    }
    edit.putString(Constants.CREDENTIALS, s)
    edit.apply()
  }
  // endregion

  fun copyToClipboard(text: String) {
    val label = getString(R.string.data_copied)
    val clipboard = mainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)

    toast(label)
  }

  fun toast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
  }
}