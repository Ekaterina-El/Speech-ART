package el.ka.speechart.view.ui.core

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import el.ka.speechart.other.Errors
import el.ka.speechart.other.Status
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel
import java.util.*

abstract class ExerciseBaseFragment(val onCloseItem: () -> Unit) : BaseFragment() {
  abstract var seekBar: SeekBar
  private var mediaPlayer: MediaPlayer? = null
  private val handler by lazy { Handler() }

  val exerciseViewModel: ExerciseViewModel by activityViewModels()

  private fun updateSeekBar() {
    val progress =
      ((mediaPlayer!!.currentPosition.toFloat() / mediaPlayer!!.duration.toFloat()) * 100).toInt()
    seekBar.progress = progress
    if (mediaPlayer!!.isPlaying) {
      handler.postDelayed(updater, 1000)
    }
  }

  private val updater by lazy {
    Runnable {
      updateSeekBar()
      exerciseViewModel.setCurrentMusicTime(mediaPlayer!!.currentPosition / 1000)
    }
  }

  fun rewindAudio(progress: Int) {
    val playPosition = (mediaPlayer!!.duration / 100) * progress
    mediaPlayer!!.seekTo(playPosition)
    exerciseViewModel.setCurrentMusicTime(mediaPlayer!!.currentPosition / 1000)
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

  override fun onDestroy() {
    super.onDestroy()
    mediaPlayer!!.release()
    mediaPlayer = null
  }

  override fun onResume() {
    super.onResume()
    if (exerciseViewModel.musicStatus.value == Status.PLAYING) mediaPlayer!!.start()
  }

  override fun onStop() {
    super.onStop()
    if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
  }

  fun goBack() {
    mediaPlayer!!.pause()
    exerciseViewModel.clearUserData()
    exerciseViewModel.setMusicStatus(Status.NO_LOADED)
    onCloseItem()
  }

  fun playPauseUserMusic() {

  }

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
    val output = requireContext().getExternalFilesDir(Context.STORAGE_SERVICE)?.absolutePath + "/$name.mp3"
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
  }

  fun togglePauseRecord() {
    if (exerciseViewModel.userMusicStatus.value == Status.PAUSED_RECORDING) {
      mediaRecorder.resume()
      exerciseViewModel.setUserMusicStatus(Status.RECORDING)
      handler.post(recorderUpdate)
    } else {
      mediaRecorder.pause()
      exerciseViewModel.setUserMusicStatus(Status.PAUSED_RECORDING)
      Status.PAUSED_RECORDING
    }
  }

  fun deleteRecord() {
    exerciseViewModel.clearUserData()
  }

  fun sendToCheck() {

  }
}