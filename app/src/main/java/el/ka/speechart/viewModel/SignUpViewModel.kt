package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Action
import el.ka.speechart.other.FieldError
import el.ka.speechart.other.UserRole
import el.ka.speechart.other.Validator.checkEmailField
import el.ka.speechart.other.Validator.checkPasswordField
import el.ka.speechart.other.Validator.checkUserNameField
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.RequestToRegSpecialist
import el.ka.speechart.service.model.User
import el.ka.speechart.service.repository.AuthRepository
import el.ka.speechart.service.repository.RequestsRepository
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

  private val _fieldErrors = MutableLiveData<List<FieldError>>(listOf())
  val fieldErrors: LiveData<List<FieldError>> get() = _fieldErrors
  private suspend fun checkFields() {
    addWork(Work.CHECK_FIELDS)

    val list = mutableListOf<FieldError>()
    checkEmailField(email.value!!)?.let { list.add(it) }
    checkUserNameField(fullName.value!!)?.let { list.add(it) }
    checkPasswordField(password.value!!, passwordRepeat.value!!)?.let { list.add(it) }

    removeWork(Work.CHECK_FIELDS)
    _fieldErrors.value = list
  }

  private val userData: User
    get() = User(
      role = if (isSpecialist.value!!) UserRole.SPECIALIST else UserRole.STUDY,
      email = email.value!!,
      fullName = fullName.value!!,
    )

  fun goRegistration() {
    val work = Work.CREATE_ACCOUNT
    addWork(work)

    viewModelScope.launch {
      checkFields()
      if (_fieldErrors.value!!.isEmpty()) {
        registration()
        clearFields()
      }
      removeWork(work)
    }
  }

  private suspend fun registration() {
    val work = Work.CREATE_ACCOUNT
    addWork(work)

    val password = password.value!!
    val request =
      if (isSpecialist.value == true) RequestToRegSpecialist("", userData, password) else null
    _error.value = when (request) {
      null -> AuthRepository.createAccount(userData, password)                  // send request
      else -> RequestsRepository.addRequestToRegistrationSpecialist(request)    // create study account

    }

    if (_error.value == null) {
      _externalAction.value = when (request) {
        null -> Action.GO_NEXT
        else -> Action.REQUEST_TO_REGISTRATION_ADDED
      }
    }

    removeWork(work)
  }

  private fun clearFields() {
    fullName.value = ""
    email.value = ""
    password.value = ""
    passwordRepeat.value = ""
    isSpecialist.value = false
  }
}