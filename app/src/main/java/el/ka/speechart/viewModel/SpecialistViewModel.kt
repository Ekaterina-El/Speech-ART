package el.ka.speechart.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.model.PerformedExercise
import el.ka.speechart.service.model.User
import el.ka.speechart.service.model.filterBy
import el.ka.speechart.service.repository.ExercisesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SpecialistViewModel(application: Application) : ViewModelWithSearchExercise(application) {
  private val _profile = MutableLiveData<User?>(null)
  val profile: LiveData<User?> get() = _profile

  fun setProfile(it: User?) {
    _profile.postValue(it)
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
      removeWork(work)
    }
  }
}