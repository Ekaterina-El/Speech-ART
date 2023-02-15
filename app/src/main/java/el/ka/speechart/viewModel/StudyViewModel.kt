package el.ka.speechart.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
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

class StudyViewModel(application: Application) : ViewModelWithSearchExercise(application) {
  private val _profile = MutableLiveData<User?>(null)
  val profile: LiveData<User?> get() = _profile

  fun setProfile(it: User?) {
    _profile.value = it
    loadPerformedExercises()
  }


  private val _performedExercises = MutableLiveData<List<PerformedExercise>>(listOf())
  val performedExercise: LiveData<List<PerformedExercise>> get() = _performedExercises

  private fun loadPerformedExercises() {
    val work = Work.LOAD_EXERCISES
    addWork(work)


    val performedExerciseList = _profile.value?.performedExercises ?: return
    viewModelScope.launch {
      _error.value = ExercisesRepository.getPerformedExercisesByListOfIds(performedExerciseList, withDetails = true) {
        _performedExercises.value = it
        val loaded = it.map { it.id }.joinToString(", ")
        Log.d("loadPerformedExercises", "Loaded (${it.size}): $loaded")
      }
      removeWork(work)
    }
  }
}