package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Credentials
import el.ka.speechart.other.Generator
import el.ka.speechart.other.UserRole
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.User
import el.ka.speechart.service.model.filterByFullNameAndEmail
import el.ka.speechart.service.repository.AuthRepository
import el.ka.speechart.service.repository.UsersRepository
import kotlinx.coroutines.launch

class OwnerViewModel(application: Application) : ViewModelWithSearchUsers(application, UserRole.ADMIN) {
  private val _newUserCredentials = MutableLiveData<Credentials?>()
  val newUserCredentials: LiveData<Credentials?> get() = _newUserCredentials
  fun afterNotifyAddedUser() {
    _newUserCredentials.value = null
  }

  fun addAdmin(user: User, credentials: Credentials) {
    val work = Work.ADD_ADMIN
    addWork(work)

    viewModelScope.launch {
      val password = Generator.genPassword()
      _error.value = AuthRepository.createAccount(user, password) {
        _newUserCredentials.value = Credentials(user.email, password)
        addAdminToLocal(it)
      }
      removeWork(work)
      _error.value = AuthRepository.login(credentials.email, credentials.password)
    }
  }

  private fun addAdminToLocal(user: User) {
    val admins = users.value!!.toMutableList()
    admins.add(0, user)
    users.value = admins
    clearSearch()
  }
  // endregion

}