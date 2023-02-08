package el.ka.speechart.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.model.User

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

  fun List<Exercise>.filterBy(context: Context, search: String) = this.filter {
    it.name.contains(search, true) ||
        context.getString(it.levelOfDifficulty.strRes).contains(search, true)
  }

  fun clearSearchExercises() {
    searchExercises.value = ""
    filterExercises()
  }
}