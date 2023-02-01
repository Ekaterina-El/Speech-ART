package el.ka.speechart.view.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import el.ka.speechart.R

class ErrorDialog(context: Context) : Dialog(context) {
  init {
    setContentView(R.layout.error_dialog)
    window!!.setLayout(
      ConstraintLayout.LayoutParams.MATCH_PARENT,
      ConstraintLayout.LayoutParams.WRAP_CONTENT,
    )
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)

    findViewById<TextView>(R.id.buttonOk).setOnClickListener { dismiss() }
  }

  private val messageTextView: TextView by lazy { findViewById(R.id.textMessage) }

  fun openConfirmDialog(message: String) {
    messageTextView.text = message
    show()
  }
}