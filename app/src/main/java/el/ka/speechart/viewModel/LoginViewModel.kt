package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Action
import el.ka.speechart.other.FieldError
import el.ka.speechart.other.Validator
import el.ka.speechart.other.Work
import el.ka.speechart.service.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : BaseViewModel(application) {
  val email = MutableLiveData("")
  val password = MutableLiveData("")

  private val _fieldErrors = MutableLiveData<List<FieldError>>(listOf())
  val fieldErrors: LiveData<List<FieldError>> get() = _fieldErrors
  private suspend fun checkFields() {
    addWork(Work.CHECK_FIELDS)

    val list = mutableListOf<FieldError>()
    Validator.checkEmailField(email.value!!)?.let { list.add(it) }
    Validator.checkPasswordField(password.value!!, password.value!!)?.let { list.add(it) }

    removeWork(Work.CHECK_FIELDS)
    _fieldErrors.value = list
  }

  fun goLogin() {
    val work = Work.SIGN_UP
    addWork(work)

    viewModelScope.launch {
      checkFields()
      if (_fieldErrors.value!!.isEmpty()) login()
      removeWork(work)
    }
  }

  private suspend fun login() {
    val work = Work.SIGN_UP
    addWork(work)

    _error.value = AuthRepository.login(email.value!!, password.value!!)
    if (_error.value == null) _externalAction.value = Action.GO_NEXT
    removeWork(work)
  }
}