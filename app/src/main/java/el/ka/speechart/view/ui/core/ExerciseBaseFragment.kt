package el.ka.speechart.view.ui.core

import android.media.MediaPlayer
import android.os.Handler
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel

abstract class ExerciseBaseFragment(val onCloseItem: () -> Unit) : BaseFragment() {
  lateinit var seekBar: SeekBar
  var mediaPlayer: MediaPlayer? = null
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
      mediaPlayer!!.prepare()
      exerciseViewModel.setMusicDuration(mediaPlayer!!.duration / 1000)

      updateSeekBar()

      mediaPlayer!!.setOnCompletionListener {
        exerciseViewModel.setIsPlayingAudio(false)
      }
    } catch (e: Exception) {
      toast(e.message ?: requireContext().getString(Errors.unknown.messageRes))
    }
  }

  fun playPauseMusic() {
    val isPlaying = exerciseViewModel.isPlayingAudio.value!!
    if (!isPlaying) {
      if (mediaPlayer == null) {
        val url = getAudioFileUrl() ?: return
        if (exerciseViewModel.preparedFileUrl.value == url) return
        prepareMusicPlayer(url)
      }
      mediaPlayer!!.start()
      updateSeekBar()

    } else {
      handler.removeCallbacks(updater)
      mediaPlayer!!.pause()
    }
    exerciseViewModel.setIsPlayingAudio(!isPlaying)
  }

  override fun onDestroy() {
    super.onDestroy()
    mediaPlayer!!.release();
    mediaPlayer = null;
  }

  override fun onResume() {
    super.onResume()
    if (exerciseViewModel.isPlayingAudio.value!!) mediaPlayer!!.start()
  }

  override fun onStop() {
    super.onStop()
    if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
  }

  fun goBack() {
    mediaPlayer!!.pause()
    exerciseViewModel.setIsPlayingAudio(false)
    onCloseItem()
  }
}