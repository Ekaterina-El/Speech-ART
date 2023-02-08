package el.ka.speechart.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import el.ka.speechart.R
import el.ka.speechart.databinding.ChangeDescriptionDialogBinding

class ChangeDescriptionDialog(context: Context, private val listener: Listener) : Dialog(context) {
  private lateinit var binding: ChangeDescriptionDialogBinding

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = ChangeDescriptionDialogBinding.inflate(LayoutInflater.from(context))
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)

    binding.buttonContinue.setOnClickListener {
      val description = binding.description.text.toString()
      listener.onSave(description)
      close()
    }
  }

  fun open(description: String) {
    binding.description.setText(description)
    show()
  }

  private fun close() {
    binding.description.setText("")
    dismiss()
  }

  companion object {
    interface Listener {
      fun onSave(description: String)
    }
  }
}