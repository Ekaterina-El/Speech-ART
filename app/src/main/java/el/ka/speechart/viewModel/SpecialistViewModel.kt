package el.ka.speechart.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.model.User
import el.ka.speechart.service.model.filterBy
import el.ka.speechart.service.repository.ExercisesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SpecialistViewModel(application: Application) : BaseViewModel(application) {
  private val context = MutableLiveData<Context>()
  fun setContext(context: Context) {
    this.context.value = context
  }

  // region TODO: delete
  private val _profile = MutableLiveData<User?>(null)
  val profile: LiveData<User?> get() = _profile

  fun setProfile(it: User?) {
    _profile.postValue(it)
  }
  // endregion

  private val _exercises = MutableLiveData<List<Exercise>>(listOf())
  val countOfExercises: Int get() = _exercises.value!!.size

  private val _filteredExercises = MutableLiveData<List<Exercise>>(listOf())
  val filteredExercises: LiveData<List<Exercise>> get() = _filteredExercises

  val searchExercises = MutableLiveData("")

  fun filterExercises() {
    val exercises = _exercises.value!!
    val search = searchExercises.value!!
    _filteredExercises.value = when (search) {
      "" -> exercises
      else -> exercises.filterBy(context.value!!, search)
    }
  }

  fun clearSearchExercises() {
    searchExercises.value = ""
    filterExercises()
  }

  fun loadExercises() {
    val work = Work.LOAD_EXERCISES
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.loadExercises {
        _exercises.value = it
        clearSearchExercises()
      }
      removeWork(work)
    }
  }

  fun addExercise(exercise: Exercise) {
    val work = Work.ADD_EXERCISE
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.addExercise(exercise) { exercise ->
        val exercises = _exercises.value!!.toMutableList()
        exercises.add(exercise)
        _exercises.value = exercises
        clearSearchExercises()
      }
      removeWork(work)
    }
  }
}