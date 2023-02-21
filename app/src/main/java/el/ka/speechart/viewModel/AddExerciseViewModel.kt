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
  private val _preparedRefFileUrl = MutableLiveData<String?>(null)
  val preparedRefFileUrl: LiveData<String?> get() = _preparedRefFileUrl

  fun setPreparedRefFileUrl(url: String?) {
    _preparedRefFileUrl.value = url
  }

  private val _levelOfDifficulty = MutableLiveData(LevelOfDifficulty.EASY)
  val levelOfDifficulty: LiveData<LevelOfDifficulty> get() = _levelOfDifficulty

  fun setLevelOfDifficulty(levelOfDifficulty: LevelOfDifficulty) {
    _levelOfDifficulty.value = levelOfDifficulty
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
      referencePronunciationUrl = _fileUrl.value,
      referencePronunciationAmplitude = _wave.value!!
    )

  var addedExercise: Exercise? = null
  fun addExercise() {
    val work = Work.ADD_EXERCISE
    addWork(work)

    viewModelScope.launch {
      val exercise = newExercise

      if (checkFields()) {
        _error.value = ExercisesRepository.addExercise(exercise, loadMediaToStorage = false) { newExercise ->
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

    _fieldErrors.value = errors
    return errors.size == 0
  }

  private val _musicStatus = MutableLiveData(Status.NO_RECORDED)
  val musicStatus: LiveData<Status> get() = _musicStatus

  fun setMusicStatus(status: Status) {
    _musicStatus.value = status
  }

  fun clearFields() {
    _musicStatus.value = Status.NO_RECORDED
    name.value = ""
    description.value = ""
    text.value = ""
    addedExercise = null
    _fieldErrors.value = listOf()
  }

  private val _currentRecordTime = MutableLiveData(0)
  val currentRecordTime: LiveData<Int> get() = _currentRecordTime
  fun setCurrentRecordTime(time: Int) {
    _currentRecordTime.value = time
  }

  private val _wave = MutableLiveData<List<Int>>(listOf())
  val wave: LiveData<List<Int>> get() = _wave
  fun setAudioWave(wave: List<Int>) {
    _wave.value = wave
  }

  private val _fileUrl = MutableLiveData("")
  val fileUrl: LiveData<String> get() = _fileUrl

  fun setUserFileUrl(output: String) {
    _fileUrl.value = output
  }

  fun uploadAudioFile() {
    val work = Work.UPLOAD_FILE
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.uploadUserAudioFile(_fileUrl.value!!) { newUrl ->
        _fileUrl.value = newUrl
      }
      removeWork(work)
    }
  }

  private val _musicDuration = MutableLiveData(0)
  val musicDuration: LiveData<Int> get() = _musicDuration
  fun setMusicDuration(time: Int) {
    _musicDuration.value = time
  }

  private val _currentMusicTime = MutableLiveData(0)
  val currentMusicTime: LiveData<Int> get() = _currentMusicTime
  fun setCurrentMusicTime(time: Int) {
    _currentMusicTime.value = time
  }

}