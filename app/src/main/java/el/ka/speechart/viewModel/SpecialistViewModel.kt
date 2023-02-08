package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.User
import el.ka.speechart.service.repository.AuthRepository
import el.ka.speechart.service.repository.UsersRepository
import kotlinx.coroutines.launch

class SpecialistViewModel(application: Application) : BaseViewModel(application) {
  private val _profile = MutableLiveData<User?>(null)
  val profile: LiveData<User?> get() = _profile

  fun loadProfile() {
    val work = Work.LOAD_USERS
    addWork(work)

    viewModelScope.launch {
      _error.value = UsersRepository.loadUser {
        _profile.postValue(it)
      }
      removeWork(work)
    }
  }
}