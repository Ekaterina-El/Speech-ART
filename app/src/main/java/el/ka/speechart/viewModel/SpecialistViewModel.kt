package el.ka.speechart.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.*
import el.ka.speechart.service.repository.ExercisesRepository
import el.ka.speechart.service.repository.ReviewRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SpecialistViewModel(application: Application) : ViewModelWithSearchExercise(application) {
  private val _profile = MutableLiveData<User?>(null)
  val profile: LiveData<User?> get() = _profile

  fun setProfile(it: User?) {
    _profile.value = it
    loadReviews()
  }

  val searchPerformedExercise = MutableLiveData("")
  fun clearSearchPerformedExercise() {
    searchPerformedExercise.value = ""
    filterPerformedExercises()
  }

  private val _filteredPerformedExercise = MutableLiveData<List<PerformedExercise>>(listOf())
  val filteredPerformedExercise: LiveData<List<PerformedExercise>> get() = _filteredPerformedExercise
  fun filterPerformedExercises() {
    val search = searchPerformedExercise.value!!
    val exercise = _performedExercises.value!!

    _filteredPerformedExercise.value = when {
      search.isEmpty() -> exercise
      else -> exercise.filterBy(context.value!!, search)
    }

  }

  private val _performedExercises = MutableLiveData<List<PerformedExercise>>(listOf())
  val countOfLoadedPerformedExercise: Int get() = _performedExercises.value!!.size
  fun loadPerformedExercises() {
    val work = Work.LOAD_PERFORMED_EXERCISE
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.getAllPerformedExercisesToCheck {
        _performedExercises.value = it
        clearSearchPerformedExercise()
      }
      Log.d("loadPerformedExercises", "Has error: ${_error.value != null}")
      removeWork(work)
    }
  }

  fun deleteLocalPerformedExercise(performedExercise: PerformedExercise) {
    _performedExercises.value = _performedExercises.value!!.filter { it.id != performedExercise.id }
    filterPerformedExercises()
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

}