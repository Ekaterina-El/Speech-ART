package el.ka.speechart.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import el.ka.speechart.R
import el.ka.speechart.databinding.AddAdminDialogBinding
import el.ka.speechart.databinding.RecoveryPasswordDialogBinding
import el.ka.speechart.other.Field
import el.ka.speechart.other.FieldError
import el.ka.speechart.other.UserRole
import el.ka.speechart.other.Validator
import el.ka.speechart.service.model.User

class RecoveryPasswordDialog(context: Context, private val confirmListener: ConfirmListener) : Dialog(context) {
  private lateinit var binding: RecoveryPasswordDialogBinding

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = RecoveryPasswordDialogBinding.inflate(LayoutInflater.from(context))
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)

    binding.buttonContinue.setOnClickListener {
      val email = binding.email.text.toString()
      if (validateFields(email)) {
        confirmListener.onContinue(email)
      }
    }
  }

  fun openConfirmDialog() {
    show()
  }

  private fun validateFields(email: String): Boolean {
    val errors = mutableListOf<FieldError>()
    Validator.checkEmailField(email)?.let { errors.add(it) }

    showErrors(errors)
    return errors.isEmpty()
  }

  fun close() {
    binding.email.setText("")
    showErrors(null)
    dismiss()
  }

  private fun showErrors(errors: List<FieldError>?) {
    binding.layoutEmail.error = ""

    if (errors == null) return

    errors.forEach {
      val field = when (it.field) {
        Field.EMAIL -> binding.layoutEmail
        else -> return
      }
      field.error = context.getString(it.errorType!!.messageRes)
    }
  }

  companion object {
    interface ConfirmListener {
      fun onContinue(email: String)
    }
  }
}