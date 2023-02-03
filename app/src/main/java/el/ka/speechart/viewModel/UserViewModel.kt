package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Action
import el.ka.speechart.other.UserRole
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.User
import el.ka.speechart.service.repository.AuthRepository
import el.ka.speechart.service.repository.UsersRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : BaseViewModel(application) {
  private val _user = MutableLiveData<User?>(null)
  val user: LiveData<User?> get() = _user

  private var _userLoaded = false
  val userLoaded: Boolean get() = _userLoaded

  private val isLoggedIn: Boolean get() = AuthRepository.currentUid != null

  fun loadCurrentUser() {
    val work = Work.LOAD_USER
    addWork(work)

    viewModelScope.launch {
      val uid = AuthRepository.currentUid
      if (isLoggedIn && uid != null) {
        _error.value = UsersRepository.loadUser(uid) {
          _userLoaded = true
          _user.value = it
        }
      } else {
        _userLoaded = true
        _user.postValue(null)
      }
      removeWork(work)
    }
  }

  fun logout() {
    AuthRepository.logout{
      _externalAction.value = Action.RESTART
    }
  }
}