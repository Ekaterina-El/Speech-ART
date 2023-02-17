package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.Work
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.model.filterBy
import el.ka.speechart.service.repository.ExercisesRepository
import kotlinx.coroutines.launch

abstract class ViewModelWithSearchExercise(application: Application) :
  BaseViewModel(application) {
  protected val exercises = MutableLiveData<List<Exercise>>(listOf())
  val countOfLoadedExercise: Int get() = exercises.value!!.size
  val searchExercise = MutableLiveData("")

  fun loadExercises() {
    val work = Work.LOAD_EXERCISES
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.loadExercises {
        exercises.value = it
        clearSearchExercise()
      }
      removeWork(work)
    }
  }

  private val _filteredExercises = MutableLiveData<List<Exercise>>(listOf())
  val filteredExercises: LiveData<List<Exercise>> get() = _filteredExercises

  fun filterExercises() {
    val search = searchExercise.value!!
    val exercise = exercises.value!!

    _filteredExercises.value = when {
      search.isEmpty() -> exercise
      else -> exercise.filterBy(context.value!!, search)
    }

  }

  fun clearSearchExercise() {
    searchExercise.value = ""
    filterExercises()
  }

  fun addExercise(exercise: Exercise) {
    val work = Work.ADD_EXERCISE
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.addExercise(exercise) { exercise ->
        val exercises = exercises.value!!.toMutableList()
        exercises.add(exercise)
        this@ViewModelWithSearchExercise.exercises.value = exercises
        clearSearchExercise()
      }
      removeWork(work)
    }
  }

  fun deleteExercise(exercise: Exercise) {
    val work = Work.DELETE_EXERCISE
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.deleteExercise(exercise) {
        exercises.value = exercises.value!!.filter { it.id != exercise.id }
        filterExercises()
      }
      removeWork(work)
    }
  }
}