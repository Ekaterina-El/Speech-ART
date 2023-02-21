package el.ka.speechart.view.ui.core.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.speechart.databinding.StudyPerfomedExerciseFragmentBinding
import el.ka.speechart.other.Action
import el.ka.speechart.other.Field
import el.ka.speechart.other.FieldError
import el.ka.speechart.view.ui.core.ExerciseBaseFragment
import el.ka.speechart.viewModel.ViewerSpecialistProfileViewModel

class StudyPerformedExerciseFragment(
  val openSpecialist: () -> Unit,
  onCloseItem: () -> Unit
) : ExerciseBaseFragment(onCloseItem) {
  private lateinit var binding: StudyPerfomedExerciseFragmentBinding
  override lateinit var seekBar: SeekBar
  override var userSeekBar: SeekBar? = null

  private val viewerSpecialistProfileViewModel by activityViewModels<ViewerSpecialistProfileViewModel>()

  private val fieldErrorsObserver = Observer<List<FieldError>> {
    it.forEach { fieldError ->
      if (fieldError.field == Field.REVIEW_TEXT) {
        binding.layoutText.error = getString(fieldError.errorType!!.messageRes)
      }
    }
  }

  fun openSpecialistProfile() {
    val specialist = exerciseViewModel.performedExercise.value?.specialistLocal
    if (specialist != null) {
      viewerSpecialistProfileViewModel.setProfile(specialist)
      openSpecialist()
    }
  }

  private val externalActionObserver = Observer<Action?> {
    if (it == Action.GO_BACK) onCloseItem()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = StudyPerfomedExerciseFragmentBinding.inflate(
      LayoutInflater.from(requireContext()), container, false
    )

    seekBar = binding.seekBarProgress
    seekBar.max = 100
    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(p0: SeekBar?, progress: Int, isUser: Boolean) {
        if (!isUser) return
        rewindAudio(progress)
      }

      override fun onStartTrackingTouch(p0: SeekBar?) {}
      override fun onStopTrackingTouch(p0: SeekBar?) {}
    })

    userSeekBar = binding.seekBarProgressUser
    userSeekBar!!.max = 100
    userSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(p0: SeekBar?, progress: Int, isUser: Boolean) {
        if (!isUser) return
        rewindUserAudio(progress)
      }

      override fun onStartTrackingTouch(p0: SeekBar?) {}
      override fun onStopTrackingTouch(p0: SeekBar?) {}
    })

    binding.seekBarUserRecordTransparencyLevel.setOnSeekBarChangeListener(object :
      SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        exerciseViewModel.setUserRecordTransparencyLevel(p1.toFloat() / 100)
      }

      override fun onStartTrackingTouch(p0: SeekBar?) {}
      override fun onStopTrackingTouch(p0: SeekBar?) {}
    })

    binding.reviewRating.setOnRatingBarChangeListener { _, rating, _ ->
      exerciseViewModel.setReviewRating(rating.toInt())
    }

    requireActivity().onBackPressedDispatcher.addCallback(this) {
      goBack()
    }

    binding.apply {
      master = this@StudyPerformedExerciseFragment
      viewModel = this@StudyPerformedExerciseFragment.exerciseViewModel
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    exerciseViewModel.work.observe(viewLifecycleOwner, workObserver)
    exerciseViewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
    exerciseViewModel.fieldError.observe(viewLifecycleOwner, fieldErrorsObserver)
  }

  override fun onStop() {
    super.onStop()
    exerciseViewModel.work.removeObserver(workObserver)
    exerciseViewModel.externalAction.removeObserver(externalActionObserver)
    exerciseViewModel.fieldError.removeObserver(fieldErrorsObserver)
  }
}
