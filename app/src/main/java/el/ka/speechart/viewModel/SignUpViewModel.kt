package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.*
import el.ka.speechart.other.Validator.checkEmailField
import el.ka.speechart.other.Validator.checkPasswordField
import el.ka.speechart.other.Validator.checkUserNameField
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
      if (_fieldErrors.value!!.isEmpty()) registration()
      removeWork(work)
    }
  }

  private suspend fun registration() {
    val work = Work.CREATE_ACCOUNT
    addWork(work)

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