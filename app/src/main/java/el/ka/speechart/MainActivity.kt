package el.ka.speechart

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import el.ka.speechart.other.Constants.CREDENTIALS
import el.ka.speechart.other.Constants.SEPARATOR
import el.ka.speechart.other.Credentials

class MainActivity : AppCompatActivity() {
  var loadingDialog: Dialog? = null
  val sharedPreferences: SharedPreferences by lazy {
    getSharedPreferences("SpeechApp", Context.MODE_PRIVATE)
  }



  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setTheme(R.style.Theme_SpeechArt)
    setContentView(R.layout.activity_main)
  }
}