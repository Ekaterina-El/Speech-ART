package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData

class LoginViewModel(application: Application) : BaseViewModel(application) {
  val email = MutableLiveData("")
  val password = MutableLiveData("")
}