package el.ka.speechart.viewModel

import android.app.Application
import android.util.Log
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

class AdminViewModel(application: Application) : BaseViewModel(application) {
  private val _admins = MutableLiveData<List<User>>(listOf())
//  val admins: LiveData<List<User>> get() = _admins


  fun loadAdmins() {
    val work = Work.LOAD_ADMINS
    addWork(work)

    viewModelScope.launch {
      _error.value = UsersRepository.loadUsersByRole(UserRole.ADMIN) {
        _admins.value = it
        clearSearch()
      }
      removeWork(work)
    }
  }

  // region Search
  private val _filteredAdmins = MutableLiveData<List<User>>(listOf())
  val filteredAdmins: LiveData<List<User>> get() = _filteredAdmins
  val search = MutableLiveData("")

  fun filterAdmins() {
    val search = search.value!!
    val admins = _admins.value!!

    _filteredAdmins.postValue(
      when {
        search.isEmpty() -> admins
        else -> admins.filterByFullNameAndEmail(search)
      }
    )
  }

  fun clearSearch() {
    search.value = ""
    filterAdmins()
  }
  // endregion

  // region Edit admin list
  private val _deletedUser = MutableLiveData<User?>(null)
  val deletedUser: LiveData<User?> get() = _deletedUser

  fun deleteAdmin(admin: User) {
    val work = Work.DELETE_ADMIN
    addWork(work)

    viewModelScope.launch {
      _error.value = UsersRepository.deleteUser(admin) {
        _deletedUser.postValue(admin)
      }
      removeWork(work)
    }
  }

  fun afterNotifyAboutUserDeleter() {
    _deletedUser.value = null
  }

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
    val admins = _admins.value!!.toMutableList()
    admins.add(0, user)
    _admins.value = admins
    clearSearch()
  }
  // endregion

}