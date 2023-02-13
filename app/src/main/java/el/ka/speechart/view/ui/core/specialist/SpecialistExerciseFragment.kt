package el.ka.speechart.view.ui.core.specialist

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import el.ka.speechart.databinding.SpecialistExerciseFragmentBinding
import el.ka.speechart.other.Errors
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel

class SpecialistExerciseFragment : BaseFragment() {
  private lateinit var binding: SpecialistExerciseFragmentBinding
  private lateinit var exerciseViewModel: ExerciseViewModel

  private val mediaPlayer by lazy { MediaPlayer() }
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
    exerciseViewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]
    binding = SpecialistExerciseFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

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

  @SuppressLint("ClickableViewAccessibility")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val exercise = SpecialistExerciseFragmentArgs.fromBundle(requireArguments()).exercise
    exerciseViewModel.setExercise(exercise)

    binding.seekBarProgress.max = 100

    /*binding.seekBarProgress.setOnTouchListener { _, _ ->
      val progress = binding.seekBarProgress.progress
      val playPosition = (mediaPlayer.duration / 100) * progress
      *//*
      val progressSeekBar = ((playPosition.toFloat() / mediaPlayer.duration.toFloat()) * 100).toInt()
      binding.seekBarProgress.progress = progressSeekBar*//*

      mediaPlayer.seekTo(playPosition)
//      updateSeekBar()
      return@setOnTouchListener false
    }*/

    binding.seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
        val playPosition = (mediaPlayer.duration / 100) * progress
        mediaPlayer.seekTo(playPosition)

        exerciseViewModel.setCurrentMusicTime(mediaPlayer.currentPosition / 1000)
//        updateSeekBar()
      }

      override fun onStartTrackingTouch(p0: SeekBar?) {}
      override fun onStopTrackingTouch(p0: SeekBar?) {}
    })

    mediaPlayer.setOnCompletionListener {
      exerciseViewModel.setIsPlayingAudio(false)
    }
  }

  fun goBack() {
    findNavController().popBackStack()
  }

  override fun onResume() {
    super.onResume()
    exerciseViewModel.exercise.observe(viewLifecycleOwner, exerciseObserver)
  }

  override fun onStop() {
    super.onStop()
    exerciseViewModel.exercise.removeObserver(exerciseObserver)
  }

  fun playPauseMusic() {
    val isPlaying = exerciseViewModel.isPlayingAudio.value!!
    if (isPlaying) {
      handler.removeCallbacks(updater)
      mediaPlayer.pause()
    } else {
      mediaPlayer.start()
      updateSeekBar()
    }
    exerciseViewModel.setIsPlayingAudio(!isPlaying)
  }

  private fun prepareMusicPlayer() {
    try {
      val url = exerciseViewModel.exercise.value!!.referencePronunciationFile!!.url
      mediaPlayer.setDataSource(url)
      mediaPlayer.prepare()
      exerciseViewModel.setMusicDuration(mediaPlayer.duration / 1000)
    } catch (e: Exception) {
      toast(e.message ?: requireContext().getString(Errors.unknown.messageRes))
    }
  }

  private val updater by lazy {
    Runnable {
      updateSeekBar()
      exerciseViewModel.setCurrentMusicTime(mediaPlayer.currentPosition / 1000)
    }
  }

  private fun updateSeekBar() {
    val progress =
      ((mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()) * 100).toInt()
    binding.seekBarProgress.progress = progress
    if (mediaPlayer.isPlaying) {
      handler.postDelayed(updater, 1000)
    }
  }

}