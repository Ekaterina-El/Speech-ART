package el.ka.speechart

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
  var loadingDialog: Dialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setTheme(R.style.Theme_SpeechArt)
    setContentView(R.layout.activity_main)
  }
}