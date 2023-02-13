package el.ka.speechart.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.speechart.service.model.Exercise

class ExerciseViewModel(application: Application) : BaseViewModel(application) {
  private val _exercise = MutableLiveData<Exercise?>(null)
  val exercise: LiveData<Exercise?> get() = _exercise

  fun setExercise(exercise: Exercise?) {
    _exercise.postValue(exercise)
  }

  private val _currentMusicTime = MutableLiveData(0)
  val currentMusicTime: LiveData<Int> get() = _currentMusicTime
  fun setCurrentMusicTime(currentPosition: Int) {
    _currentMusicTime.value = currentPosition
  }

  private val _isPlayingAudio = MutableLiveData(false)
  val isPlayingAudio: LiveData<Boolean> get() = _isPlayingAudio

  fun setIsPlayingAudio(newValue: Boolean) {
    _isPlayingAudio.value = newValue
  }

  private val _musicDuration = MutableLiveData(0)
  val musicDuration: LiveData<Int> get() = _musicDuration
  fun setMusicDuration(duration: Int) {
    _musicDuration.value = duration
  }
}