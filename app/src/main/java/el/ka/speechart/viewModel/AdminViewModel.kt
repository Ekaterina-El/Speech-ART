package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.UserRole
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.User
import el.ka.speechart.service.repository.UsersRepository
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : BaseViewModel(application) {
  private val _admins = MutableLiveData<List<User>>(listOf())
  val admins: LiveData<List<User>> get() = _admins

  fun loadAdmins() {
    val work = Work.LOAD_ADMINS
    addWork(work)

    viewModelScope.launch {
      _error.value = UsersRepository.loadUsersByRole(UserRole.ADMIN) {
        _admins.value = it
      }
      removeWork(work)
    }
  }
}