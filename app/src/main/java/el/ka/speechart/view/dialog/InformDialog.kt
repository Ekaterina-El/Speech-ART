package el.ka.speechart.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import el.ka.speechart.R
import el.ka.speechart.databinding.InformDialogBinding

class InformDialog(context: Context) : Dialog(context) {
  private var binding: InformDialogBinding

  init {
    binding = InformDialogBinding.inflate(LayoutInflater.from(context))
    setContentView(binding.root)
    window!!.setLayout(
      ConstraintLayout.LayoutParams.MATCH_PARENT,
      ConstraintLayout.LayoutParams.WRAP_CONTENT,
    )
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)

    binding.buttonOk.setOnClickListener { dismiss() }
  }


  fun open(
    title: String,
    message: String,
    warning: String? = null,
    onClickOk: (() -> Unit)? = null,
    onTextClicked: (() -> Unit)? = null
  ) {
    binding.title.text = title
    binding.textMessage.text = message
    binding.textWarning.text = warning ?: ""
    binding.textWarning.visibility = if (warning != null) View.VISIBLE else View.INVISIBLE
    binding.textMessage.setOnClickListener { onTextClicked?.let { it() } }
    binding.buttonOk.setOnClickListener {
      onClickOk?.let { f -> f() }
      dismiss()
    }

    show()
  }
}