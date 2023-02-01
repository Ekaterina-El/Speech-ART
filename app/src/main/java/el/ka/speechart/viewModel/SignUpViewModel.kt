package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Action
import el.ka.speechart.other.ErrorApp
import el.ka.speechart.other.UserRole
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.User
import el.ka.speechart.service.repository.AuthRepository
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : BaseViewModel(application) {
  val fullName = MutableLiveData("")
  val email = MutableLiveData("")
  val password = MutableLiveData("")
  val passwordRepeat = MutableLiveData("")

  private val isSpecialist = MutableLiveData(false)

  fun setIsSpecialist(value: Boolean) {
    isSpecialist.value = value
  }

  private val _error = MutableLiveData<ErrorApp?>(null)
  val error: LiveData<ErrorApp?> = _error

  private val userData: User get() = User(
    role = if (isSpecialist.value!!) UserRole.SPECIALIST else UserRole.STUDY,
    email = email.value!!,
    fullName = fullName.value!!,
  )

  fun goRegistration() {
    val work = Work.CREATE_ACCOUNT
    addWork(work)

    viewModelScope.launch {
      if (isSpecialist.value == true) {
        // send request
      } else {
        // create study account
      }

      _error.value = AuthRepository.createAccount(userData, password.value!!)
      if (_error.value == null) _externalAction.value = Action.GO_NEXT
      removeWork(work)
    }
  }
}