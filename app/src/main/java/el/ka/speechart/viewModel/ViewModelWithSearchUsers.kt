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

abstract class ViewModelWithSearchUsers(application: Application, private val usersRoleForDownload: UserRole) : BaseViewModel(application) {
  protected val users = MutableLiveData<List<User>>(listOf())
  val countOfLoadedUsers: Int get() = users.value!!.size
  val search = MutableLiveData("")

  fun loadUsers() {
    val work = Work.LOAD_USERS
    addWork(work)

    viewModelScope.launch {
      _error.value = UsersRepository.loadUsersByRole(usersRoleForDownload) {
        users.value = it
        clearSearch()
      }
      removeWork(work)
    }
  }

  private val _filteredUsers = MutableLiveData<List<User>>(listOf())
  val filteredUsers: LiveData<List<User>> get() = _filteredUsers

  fun filterUsers() {
    val search = search.value!!
    val users = users.value!!

    _filteredUsers.postValue(
      when {
        search.isEmpty() -> users
        else -> users.filterByFullNameAndEmail(search)
      }
    )
  }

  fun clearSearch() {
    search.value = ""
    filterUsers()
  }

  // region Edit users
  private val _deletedUser = MutableLiveData<User?>(null)
  val deletedUser: LiveData<User?> get() = _deletedUser

  fun deleteUser(user: User) {
    val work = Work.DELETE_USER
    addWork(work)

    viewModelScope.launch {
      _error.value = UsersRepository.deleteUser(user) {
        _deletedUser.postValue(user)
      }
      removeWork(work)
    }
  }

  fun afterNotifyAboutUserDeleter() {
    _deletedUser.value = null
  }
  // endregion
}