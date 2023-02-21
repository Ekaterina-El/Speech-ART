package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.speechart.other.*
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.repository.ExercisesRepository
import kotlinx.coroutines.launch

class AddExerciseViewModel(application: Application) : BaseViewModel(application) {
  private val _levelOfDifficulty = MutableLiveData(LevelOfDifficulty.EASY)
  val levelOfDifficulty: LiveData<LevelOfDifficulty> get() = _levelOfDifficulty

  fun setLevelOfDifficulty(levelOfDifficulty: LevelOfDifficulty) {
    _levelOfDifficulty.value = levelOfDifficulty
  }

  private var _mediaFileInfo = MutableLiveData<MediaFileInfo?>(null)
  val mediaFileInfo: LiveData<MediaFileInfo?> get() = _mediaFileInfo

  fun setPickedFile(mediaFileInfo: MediaFileInfo?) {
    _mediaFileInfo.value = mediaFileInfo
  }

  val name = MutableLiveData("")
  val description = MutableLiveData("")
  val text = MutableLiveData("")

  private val newExercise: Exercise
    get() = Exercise(
      name = name.value!!,
      description = description.value!!,
      text = text.value!!,
      levelOfDifficulty = _levelOfDifficulty.value!!,
      referencePronunciationFile = mediaFileInfo.value
    )

  var addedExercise: Exercise? = null
  fun addExercise() {
    val work = Work.ADD_EXERCISE
    addWork(work)

    viewModelScope.launch {
      val exercise = newExercise

      if (checkFields()) {
        ExercisesRepository.addExercise(exercise) { newExercise ->
          addedExercise = newExercise
          _externalAction.value = Action.GO_BACK
        }
      }
      removeWork(work)
    }
  }

  private val _fieldErrors = MutableLiveData<List<FieldError>>(listOf())
  val fieldErrors: LiveData<List<FieldError>> get() = _fieldErrors

  private fun checkFields(): Boolean {
    val errors = mutableListOf<FieldError>()
    if (name.value!!.isEmpty()) {
      errors.add(FieldError(Field.NAME, FieldErrorType.IS_REQUIRE))
    }
    if (description.value!!.isEmpty()) {
      errors.add(FieldError(Field.DESCRIPTION, FieldErrorType.IS_REQUIRE))
    }
    if (text.value!!.isEmpty()) {
      errors.add(FieldError(Field.EXERCISE_TEXT, FieldErrorType.IS_REQUIRE))
    }
    if (mediaFileInfo.value == null) {
      errors.add(FieldError(Field.MEDIA_FILE, FieldErrorType.IS_REQUIRE))
    }

    _fieldErrors.value = errors
    return errors.size == 0
  }

  fun clearFields() {
    _mediaFileInfo.value = null
    name.value = ""
    description.value = ""
    text.value = ""
    addedExercise = null
    _fieldErrors.value = listOf()
  }
}