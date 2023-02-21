package el.ka.speechart.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.rpc.context.AttributeContext.Auth
import el.ka.speechart.other.*
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.service.model.PerformedExercise
import el.ka.speechart.service.repository.AuthRepository
import el.ka.speechart.service.repository.ExercisesRepository
import el.ka.speechart.service.repository.ReviewRepository
import el.ka.speechart.service.repository.UsersRepository
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : BaseViewModel(application) {
  private val _exercise = MutableLiveData<Exercise?>(null)
  val exercise: LiveData<Exercise?> get() = _exercise

  fun setExercise(exercise: Exercise?) {
    _exercise.value = exercise
  }

  private val _performedExercise = MutableLiveData<PerformedExercise?>(null)
  val performedExercise: LiveData<PerformedExercise?> get() = _performedExercise

  fun setPerformedExercise(performedExercise: PerformedExercise) {
    setExercise(performedExercise.exerciseLocal)
    _userMusicStatus.value = Status.NO_LOADED
    _userFileUrl.value = performedExercise.userAudioFileUrl
    _performedExercise.value = performedExercise
  }

  private val _preparedFileUrl = MutableLiveData<String?>(null)
  val preparedFileUrl: LiveData<String?> get() = _preparedFileUrl

  fun setPrepareFileUrl(url: String?) {
    _preparedFileUrl.value = url
  }

  private val _currentMusicTime = MutableLiveData(0)
  val currentMusicTime: LiveData<Int> get() = _currentMusicTime
  fun setCurrentMusicTime(currentPosition: Int) {
    _currentMusicTime.value = currentPosition
  }

  private val _musicDuration = MutableLiveData(0)
  val musicDuration: LiveData<Int> get() = _musicDuration
  fun setMusicDuration(duration: Int) {
    _musicDuration.value = duration
  }

  private val _musicStatus = MutableLiveData(Status.NO_LOADED)
  val musicStatus: LiveData<Status> get() = _musicStatus
  fun setMusicStatus(status: Status) {
    Log.d("setMusicStatus", "Status: $status")
      _musicStatus.value = status
  }

  fun clearUserData() {
    _musicStatus.value = Status.NO_LOADED
    _userMusicStatus.value = Status.NO_RECORDED
    _currentUserRecordTime.value = 0
    _userMusicDuration.value = 0
    _currentMusicTime.value = 0
    conclusion.value = ""
    _fieldErrors.value = listOf()
    if (_userFileUrl.value == null) removeUserAudioFile()
  }

  private val _userMusicStatus = MutableLiveData(Status.NO_RECORDED)
  val userMusicStatus: LiveData<Status> get() = _userMusicStatus
  fun setUserMusicStatus(status: Status) {
    _userMusicStatus.value = status
  }

  private val _currentUserRecordTime = MutableLiveData(0)
  val currentUserRecordTime: LiveData<Int> get() = _currentUserRecordTime
  fun setCurrentUserRecordTime(time: Int) {
    Log.d("setCurrentUserRecordTime", "Time: $time")
    _currentUserRecordTime.value = time
  }

  private val _userMusicDuration = MutableLiveData(0)
  val userMusicDuration: LiveData<Int> get() = _userMusicDuration
  fun setUserMusicDuration(time: Int) {
    _userMusicDuration.value = time
  }

  private val _currentUserMusicTime = MutableLiveData(0)
  val currentUserMusicTime: LiveData<Int> get() = _currentUserMusicTime
  fun setCurrentUserMusicTime(time: Int) {
    _currentUserMusicTime.value = time
  }

  private val _userFileUrl = MutableLiveData("")
  val userFileUrl: LiveData<String> get() = _userFileUrl
  fun setUserFileUrl(output: String) {
    _userFileUrl.value = output
  }

  private val _preparedUserFileUrl = MutableLiveData<String?>(null)
  val preparedUserFileUrl: LiveData<String?> get() = _preparedUserFileUrl
  fun setPrepareUserFileUrl(url: String) {
    _preparedUserFileUrl.value = url
  }

  private fun removeUserAudioFile() {
    val work = Work.REMOVER_FILE
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.removeUserAudioFile(_preparedUserFileUrl.value!!)
      removeWork(work)
    }
  }

  fun uploadAudioFile() {
    val work = Work.UPLOAD_FILE
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.uploadUserAudioFile(_userFileUrl.value!!) { newUrl ->
        _userFileUrl.value = newUrl
      }
      removeWork(work)
    }
  }

  val newPerformedExercise: PerformedExercise get() = PerformedExercise(
    user = AuthRepository.currentUid ?: "",
    userAudioFileUrl = _userFileUrl.value!!,
    exerciseId = _exercise.value!!.id,
  )

  fun sendToCheck() {
    val work = Work.ADD_EXERCISE
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.sendExercise(newPerformedExercise)
      _externalAction.value = Action.GO_BACK
      clearUserData()
      removeWork(work)
    }
  }

  private val _fieldErrors = MutableLiveData<List<FieldError>>(listOf())
  val fieldError: LiveData<List<FieldError>> get() = _fieldErrors

  val conclusion = MutableLiveData("")
  fun sendConclusion() {
    if (conclusion.value!!.isEmpty()) {
      _fieldErrors.value = listOf(
        FieldError(Field.CONCLUSION, errorType = FieldErrorType.IS_REQUIRE)
      )
      return
    }

    val work = Work.SEND_CONCLUSION
    addWork(work)

    viewModelScope.launch {
      _error.value = ExercisesRepository.sendConclusion(_performedExercise.value!!, conclusion.value!!, AuthRepository.currentUid!!) {
        _externalAction.value = Action.DELETE_AND_GO_BACK
      }
      removeWork(work)
    }
  }

  // region Review
  private val _reviewRating = MutableLiveData(1)
  val reviewRating: LiveData<Int> get() = _reviewRating

  fun setReviewRating(rating: Int) {
    _reviewRating.value = rating
  }

  val reviewText = MutableLiveData("")

  fun sendReview() {
    if (reviewText.value!! == "") {
      _fieldErrors.value = listOf(
        FieldError(field = Field.REVIEW_TEXT, FieldErrorType.IS_REQUIRE)
      )
      return
    }

    val work = Work.SEND_REVIEW
    addWork(work)

    viewModelScope.launch {
      _error.value = ReviewRepository.sendReview(_performedExercise.value!!, _reviewRating.value!!, reviewText.value!!) { review ->
        val performedExercise = _performedExercise.value!!
        performedExercise.reviewId = review.id
        performedExercise.reviewLocal = review
        _performedExercise.value = performedExercise
      }
      removeWork(work)
    }
  }
  // endregion
}