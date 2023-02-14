package el.ka.speechart.view.ui.core.specialist

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import el.ka.speechart.databinding.SpecialistExerciseFragmentBinding
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel

class SpecialistExerciseFragment(val exercise: Exercise?,  val onCloseItem: () -> Unit) : BaseFragment() {
  private lateinit var binding: SpecialistExerciseFragmentBinding
  private val exerciseViewModel: ExerciseViewModel by activityViewModels()

  private var mediaPlayer: MediaPlayer? = null
  private val handler by lazy { Handler() }

  private val exerciseObserver = Observer<Exercise?> {
    if (it == null) return@Observer
    prepareMusicPlayer()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SpecialistExerciseFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    binding.seekBarProgress.max = 100
    binding.seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(p0: SeekBar?, progress: Int, isUser: Boolean) {
        if (!isUser) return
        val playPosition = (mediaPlayer!!.duration / 100) * progress
        mediaPlayer!!.seekTo(playPosition)
        exerciseViewModel.setCurrentMusicTime(mediaPlayer!!.currentPosition / 1000)
      }

      override fun onStartTrackingTouch(p0: SeekBar?) {}
      override fun onStopTrackingTouch(p0: SeekBar?) {}
    })

    requireActivity().onBackPressedDispatcher.addCallback(this) {
      goBack()
    }

    binding.apply {
      master = this@SpecialistExerciseFragment
      viewModel = this@SpecialistExerciseFragment.exerciseViewModel
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

  fun goBack() {
    mediaPlayer!!.pause()
    exerciseViewModel.setIsPlayingAudio(false)
    onCloseItem()
  }

  override fun onResume() {
    super.onResume()
    if (exerciseViewModel.isPlayingAudio.value!!) mediaPlayer!!.start()
    exerciseViewModel.exercise.observe(viewLifecycleOwner, exerciseObserver)
  }

  override fun onStop() {
    super.onStop()
    if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
    exerciseViewModel.exercise.removeObserver(exerciseObserver)
  }

  fun playPauseMusic() {
    val isPlaying = exerciseViewModel.isPlayingAudio.value!!
    if (isPlaying) {
      handler.removeCallbacks(updater)
      mediaPlayer!!.pause()
    } else {
      mediaPlayer!!.start()
      updateSeekBar()
    }
    exerciseViewModel.setIsPlayingAudio(!isPlaying)
  }

  override fun onDestroy() {
    super.onDestroy()
    mediaPlayer!!.release();
    mediaPlayer = null;
  }

  private fun prepareMusicPlayer() {
    try {
      val exercise = exerciseViewModel.exercise.value ?: return
      val referencePronunciationFile = exercise.referencePronunciationFile ?: return
      val url = referencePronunciationFile.url

      if (exerciseViewModel.preparedFileUrl.value == url) return
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

  private val updater by lazy {
    Runnable {
      updateSeekBar()
      exerciseViewModel.setCurrentMusicTime(mediaPlayer!!.currentPosition / 1000)
    }
  }

  private fun updateSeekBar() {
    val progress =
      ((mediaPlayer!!.currentPosition.toFloat() / mediaPlayer!!.duration.toFloat()) * 100).toInt()
    binding.seekBarProgress.progress = progress
    if (mediaPlayer!!.isPlaying) {
      handler.postDelayed(updater, 1000)
    }
  }
}