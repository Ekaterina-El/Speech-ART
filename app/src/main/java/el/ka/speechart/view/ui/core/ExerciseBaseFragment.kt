package el.ka.speechart.view.ui.core

import android.media.MediaPlayer
import android.os.Handler
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import el.ka.speechart.other.Errors
import el.ka.speechart.other.Status
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel

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
}