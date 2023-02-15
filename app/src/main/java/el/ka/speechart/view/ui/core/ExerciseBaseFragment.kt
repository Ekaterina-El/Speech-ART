package el.ka.speechart.view.ui.core

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import el.ka.speechart.other.*
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel
import java.util.*

abstract class ExerciseBaseFragment(val onCloseItem: () -> Unit) : BaseFragment() {
  abstract var seekBar: SeekBar
  abstract var userSeekBar: SeekBar?

  private var mediaPlayer: MediaPlayer? = null
  private var userMediaPlayer: MediaPlayer? = null
  private val handler by lazy { Handler() }

  val exerciseViewModel: ExerciseViewModel by activityViewModels()

  private fun updateSeekBar() {
    seekBar.progress = mediaPlayer!!.progress
    if (mediaPlayer!!.isPlaying) {
      handler.postDelayed(updater, 1000)
    }
  }

  private val updater by lazy {
    Runnable {
      updateSeekBar()
      exerciseViewModel.setCurrentMusicTime(mediaPlayer!!.timeInSeconds)
    }
  }

  fun rewindAudio(progress: Int) {
    mediaPlayer!!.playAt(progress)
    exerciseViewModel.setCurrentMusicTime(mediaPlayer!!.timeInSeconds)
  }

  fun rewindUserAudio(progress: Int) {
    userMediaPlayer!!.playAt(progress)
    exerciseViewModel.setCurrentUserMusicTime(userMediaPlayer!!.timeInSeconds)
  }

  private fun getAudioFileUrl(): String? =
    exerciseViewModel.exercise.value?.referencePronunciationFile?.url


  private fun prepareMusicPlayer(url: String) {
    try {
      exerciseViewModel.setPrepareFileUrl(url)

      mediaPlayer = MediaPlayer()
      mediaPlayer!!.setDataSource(url)
      exerciseViewModel.setMusicStatus(Status.LOADING)
      mediaPlayer!!.setOnPreparedListener {
        exerciseViewModel.setMusicDuration(mediaPlayer!!.duration / 1000)
        exerciseViewModel.setMusicStatus(Status.PLAYING)
        mediaPlayer!!.start()
        updateSeekBar()
      }
      mediaPlayer!!.prepareAsync()


      mediaPlayer!!.setOnCompletionListener {
        exerciseViewModel.setMusicStatus(Status.PAUSED)
      }
    } catch (e: Exception) {
      toast(e.message ?: requireContext().getString(Errors.unknown.messageRes))
    }
  }

  fun playPauseMusic() {
    if (exerciseViewModel.userMusicStatus.value == Status.RECORDING) return

    val musicStatus = exerciseViewModel.musicStatus.value!!
    if (musicStatus == Status.PAUSED || musicStatus == Status.NO_LOADED) {
      if (mediaPlayer == null) {
        val url = getAudioFileUrl() ?: return
        if (exerciseViewModel.preparedFileUrl.value == url) return
        prepareMusicPlayer(url)
        return
      } else {
        mediaPlayer!!.start()
        updateSeekBar()
      }
    } else if (musicStatus == Status.PLAYING) {
      handler.removeCallbacks(updater)
      mediaPlayer!!.pause()
    }
    exerciseViewModel.setMusicStatus(if (musicStatus == Status.PLAYING) Status.PAUSED else Status.PLAYING)
  }

  private fun destroyMediaPlayer() {
    mediaPlayer?.release()
    mediaPlayer = null

    userMediaPlayer?.release()
    userMediaPlayer = null
  }

  override fun onDestroy() {
    super.onDestroy()
    destroyMediaPlayer()
  }

  override fun onResume() {
    super.onResume()
    if (exerciseViewModel.musicStatus.value == Status.PLAYING) mediaPlayer!!.start()
    if (exerciseViewModel.userMusicStatus.value == Status.PLAYING) userMediaPlayer!!.start()
  }

  override fun onStop() {
    super.onStop()
    if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
    if (userMediaPlayer?.isPlaying == true) userMediaPlayer?.pause()
  }

  fun goBack() {
    destroyMediaPlayer()
    exerciseViewModel.clearUserData()
    exerciseViewModel.setMusicStatus(Status.NO_LOADED)
    onCloseItem()
  }

  fun playPauseUserMusic() {
    if (exerciseViewModel.userMusicStatus.value == Status.RECORDING) return

    val musicStatus = exerciseViewModel.userMusicStatus.value!!
    if (musicStatus == Status.PAUSED || musicStatus == Status.NO_LOADED || musicStatus == Status.RECORDED) {
      if (userMediaPlayer == null) {
        val url = exerciseViewModel.userFileUrl.value ?: return
        if (exerciseViewModel.preparedUserFileUrl.value == url) return
        prepareUserMusicPlayer(url)
        return
      } else {
        userMediaPlayer!!.start()
        updateUserSeekBar()
      }
    } else if (musicStatus == Status.PLAYING) {
      handler.removeCallbacks(updater)
      userMediaPlayer!!.pause()
    }
    exerciseViewModel.setUserMusicStatus(if (musicStatus == Status.PLAYING) Status.PAUSED else Status.PLAYING)
  }

  private fun prepareUserMusicPlayer(url: String) {
    try {
      exerciseViewModel.setPrepareUserFileUrl(url)
      userMediaPlayer = MediaPlayer()
      userMediaPlayer!!.setDataSource(url)
      exerciseViewModel.setUserMusicStatus(Status.LOADING)

      userMediaPlayer!!.setOnPreparedListener {
        exerciseViewModel.setUserMusicDuration(userMediaPlayer!!.duration / 1000)
        exerciseViewModel.setUserMusicStatus(Status.PLAYING)
        userMediaPlayer!!.start()
        updateUserSeekBar()
      }
      userMediaPlayer!!.prepareAsync()


      userMediaPlayer!!.setOnCompletionListener {
        exerciseViewModel.setUserMusicStatus(Status.PAUSED)
      }
    } catch (e: Exception) {
      toast(e.message ?: requireContext().getString(Errors.unknown.messageRes))
    }
  }

  private fun updateUserSeekBar() {
    userSeekBar!!.progress = userMediaPlayer!!.progress
    if (userMediaPlayer!!.isPlaying) {
      handler.postDelayed(userUpdater, 1000)
    }
  }

  private val userUpdater by lazy {
    Runnable {
      updateUserSeekBar()
      exerciseViewModel.setCurrentUserMusicTime(userMediaPlayer!!.currentPosition / 1000)
    }
  }

  // region Recorder
  private lateinit var mediaRecorder: MediaRecorder

  private val recorderUpdate: Runnable by lazy {
    Runnable {
      if (exerciseViewModel.userMusicStatus.value == Status.RECORDING) {
        exerciseViewModel.setCurrentUserRecordTime(exerciseViewModel.currentUserRecordTime.value!! + 1)
        handler.postDelayed(recorderUpdate, 1000)
      }
    }
  }

  fun startRecord() {
    if (mediaPlayer?.isPlaying == true) playPauseMusic()

    val name = Calendar.getInstance().time.toString().split(" ").joinToString("-")
    val output =
      requireContext().getExternalFilesDir(Context.STORAGE_SERVICE)?.absolutePath + "/$name.mp3"
    exerciseViewModel.setUserFileUrl(output)
    mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      MediaRecorder(requireContext())
    } else {
      MediaRecorder()
    }
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    mediaRecorder.setOutputFile(output)

    mediaRecorder.prepare()
    mediaRecorder.start()

    exerciseViewModel.setUserMusicStatus(Status.RECORDING)
    handler.post(recorderUpdate)
  }

  fun stopRecord() {
    exerciseViewModel.setUserMusicStatus(Status.RECORDED)
    mediaRecorder.stop()
    mediaRecorder.release()
    exerciseViewModel.uploadAudioFile()
  }

  fun togglePauseRecord() {
    if (exerciseViewModel.userMusicStatus.value == Status.PAUSED_RECORDING) {
      mediaRecorder.resume()
      exerciseViewModel.setUserMusicStatus(Status.RECORDING)
      handler.post(recorderUpdate)
    } else {
      mediaRecorder.pause()
      exerciseViewModel.setUserMusicStatus(Status.PAUSED_RECORDING)
    }
  }

  fun deleteRecord() {
    if (userMediaPlayer != null) {
      userMediaPlayer!!.pause()
      userMediaPlayer!!.release()
      userMediaPlayer = null
    }

    exerciseViewModel.clearUserData()
  }
  // endregion

  fun sendToCheck() {
    if (userMediaPlayer != null) {
      userMediaPlayer!!.pause()
      userMediaPlayer!!.release()
      userMediaPlayer = null
    }
    exerciseViewModel.sendToCheck()
  }
}