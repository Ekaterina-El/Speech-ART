package el.ka.speechart.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager.BadTokenException
import el.ka.speechart.R
import el.ka.speechart.databinding.ConfirmDialogBinding

class ConfirmDialog(context: Context) : Dialog(context) {
  private lateinit var binding: ConfirmDialogBinding

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = ConfirmDialogBinding.inflate(LayoutInflater.from(context))
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)
  }

  fun openConfirmDialog(message: String, confirmListener: ConfirmListener, value: Any? = null) {
    try {
      binding.buttonYes.setOnClickListener { confirmListener.onAgree(value) }
      binding.buttonCancel.setOnClickListener { confirmListener.onDisagree() }
      binding.textViewMessage.text = message
      show()
    } catch (_: BadTokenException) {
    }
  }

  fun closeConfirmDialog() {
    binding.buttonYes.setOnClickListener(null)
    binding.buttonCancel.setOnClickListener(null)
    binding.textViewMessage.text = ""
    dismiss()
  }

  companion object {
    interface ConfirmListener {
      fun onAgree(value: Any? = null)
      fun onDisagree()
    }

    private var dialog: ConfirmDialog? = null
    fun getInstance(context: Context): ConfirmDialog {
      dialog = if (dialog == null) ConfirmDialog(context) else dialog
      return dialog!!
    }
  }
}