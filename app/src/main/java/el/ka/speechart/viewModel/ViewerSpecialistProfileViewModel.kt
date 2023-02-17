package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.Review
import el.ka.speechart.service.model.User
import el.ka.speechart.service.repository.AuthRepository
import el.ka.speechart.service.repository.ReviewRepository
import el.ka.speechart.service.repository.UsersRepository
import kotlinx.coroutines.launch

class ViewerSpecialistProfileViewModel(application: Application) : BaseViewModel(application) {
  private val _profile = MutableLiveData<User?>(null)
  val profile: LiveData<User?> get() = _profile

  fun setProfile(profile: User) {
    _profile.value = profile
    loadReviews()
  }

  private val _reviews = MutableLiveData<List<Review>>(listOf())
  val reviews: LiveData<List<Review>> get() = _reviews

  private fun loadReviews() {
    val work = Work.LOAD_REVIEWS
    addWork(work)

    viewModelScope.launch {
      _error.value = ReviewRepository.getReviewsByListOfIds(_profile.value!!.reviews) { reviews ->
        _reviews.value = reviews
      }
      removeWork(work)
    }
  }

  fun uploadProfile() {
    val work = Work.LOAD_USER
    addWork(work)

    viewModelScope.launch {
      val uid = _profile.value!!.uid
      _error.value = UsersRepository.loadUser(uid) {
        setProfile(it)
      }
      removeWork(work)
    }
  }
}