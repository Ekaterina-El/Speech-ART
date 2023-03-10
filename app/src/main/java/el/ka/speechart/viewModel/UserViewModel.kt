package el.ka.speechart.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Action
import el.ka.speechart.other.UserRole
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.Review
import el.ka.speechart.service.model.User
import el.ka.speechart.service.repository.AuthRepository
import el.ka.speechart.service.repository.ReviewRepository
import el.ka.speechart.service.repository.UsersRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : BaseViewModel(application) {
  private val _user = MutableLiveData<User?>(null)
  val user: LiveData<User?> get() = _user

  private var _userLoaded = false
  val userLoaded: Boolean get() = _userLoaded

  fun loadCurrentUser() {
    val work = Work.LOAD_USER
    addWork(work)

    viewModelScope.launch {
      val uid = AuthRepository.currentUid
      Log.d("loadCurrentUser", "uid: $uid")
      if (uid != null) {
        _error.value = UsersRepository.loadUser(uid) {
          Log.d("loadCurrentUser", "loaded")
          _userLoaded = true
          _user.value = it
        }
      } else {
        _userLoaded = true
        _user.value = null
      }
      removeWork(work)
    }
  }

  fun logout() {
    AuthRepository.logout{
      _externalAction.value = Action.RESTART
    }
  }

  fun updateProfileImage(uri: Uri) {
    val work = Work.UPDATE_USER
    addWork(work)

    viewModelScope.launch {
      _error.value = UsersRepository.updateProfileImage(_user.value!!.profileUrl, uri) { url ->
        val user = _user.value!!
        user.profileUrl = url
        _user.postValue(user)
      }
      removeWork(work)
    }
  }

  fun updateDescription(newDescription: String) {
    val work = Work.UPDATE_USER
    addWork(work)

    viewModelScope.launch {
      _error.value = UsersRepository.updateUserDescription(newDescription) {
        val user = _user.value!!
        user.description = newDescription
        _user.postValue(user)
      }
      removeWork(work)
    }
  }

  fun sendRecoveryPasswordMail(email: String) {
    val work = Work.SEND_RECOVERY_PASSWORD_EMAIL
    addWork(work)

    viewModelScope.launch {
      _error.value = AuthRepository.sendRecoveryPasswordMail(email)
      removeWork(work)
    }
  }
}