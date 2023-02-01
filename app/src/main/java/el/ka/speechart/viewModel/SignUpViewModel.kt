package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData

class SignUpViewModel(application: Application) : BaseViewModel(application) {
  val fullName = MutableLiveData("")
  val email = MutableLiveData("")
  val password = MutableLiveData("")
  val passwordRepeat = MutableLiveData("")

  val isSpecialist = MutableLiveData(false)
  fun setIsSpecialist(value: Boolean) {
    isSpecialist.value = value
  }
}