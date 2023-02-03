package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.UserRole
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.User
import el.ka.speechart.service.model.filterByFullNameAndEmail
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

    _filteredAdmins.value = when {
      search.isEmpty() -> admins
      else -> admins.filterByFullNameAndEmail(search)
    }
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
  // endregion

}