package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.speechart.other.UserRole
import el.ka.speechart.service.model.User
import el.ka.speechart.service.repository.AuthRepository

class UserViewModel(application: Application) : BaseViewModel(application) {
  private val _user = MutableLiveData<User?>(null)
  val user: LiveData<User?> get() = _user

  private var _userLoaded = false
  val userLoaded: Boolean get() = _userLoaded

  private val isLoggedIn: Boolean get() = AuthRepository.currentUid != null

  fun loadCurrentUser() {
    _userLoaded = true

    if (isLoggedIn) {
      _user.value = User("123", UserRole.STUDY)
    } else {
      _user.value = null
    }

  }
}