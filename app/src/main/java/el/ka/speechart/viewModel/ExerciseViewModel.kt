package el.ka.speechart.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.speechart.other.Status
import el.ka.speechart.service.model.Exercise

class ExerciseViewModel(application: Application) : BaseViewModel(application) {
  private val _exercise = MutableLiveData<Exercise?>(null)
  val exercise: LiveData<Exercise?> get() = _exercise

  fun setExercise(exercise: Exercise?) {
    _exercise.postValue(exercise)
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
    _userMusicStatus.value = Status.NO_RECORDED
    _currentUserRecordTime.value = 0
    _userMusicDuration.value = 0
    _currentMusicTime.value = 0
    _userFileUrl.value = ""
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


}