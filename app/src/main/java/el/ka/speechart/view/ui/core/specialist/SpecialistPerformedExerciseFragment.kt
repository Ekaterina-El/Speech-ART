package el.ka.speechart.view.ui.core.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.speechart.databinding.SpecialistExerciseFragmentBinding
import el.ka.speechart.databinding.SpecialistPerfomedExerciseFragmentBinding
import el.ka.speechart.databinding.StudyExerciseFragmentBinding
import el.ka.speechart.databinding.StudyPerfomedExerciseFragmentBinding
import el.ka.speechart.other.Action
import el.ka.speechart.other.Field
import el.ka.speechart.other.FieldError
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.view.ui.core.ExerciseBaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel
import el.ka.speechart.viewModel.SpecialistViewModel

class SpecialistPerformedExerciseFragment(onCloseItem: () -> Unit) : ExerciseBaseFragment(onCloseItem) {
  private lateinit var binding: SpecialistPerfomedExerciseFragmentBinding
  override lateinit var seekBar: SeekBar

  private val specialistViewModel: SpecialistViewModel by activityViewModels()

  override var userSeekBar: SeekBar? = null

  private val fieldErrorsObserver = Observer<List<FieldError>> {
    binding.layoutText.error = ""
    for (fieldError in it) {
      if (fieldError.field == Field.CONCLUSION) binding.layoutText.error = getString(fieldError.errorType!!.messageRes)
    }
  }

  private val externalActionObserver = Observer<Action?> {
    if (it == Action.GO_BACK) onCloseItem()
    else if (it == Action.DELETE_AND_GO_BACK) {
      specialistViewModel.deleteLocalPerformedExercise(exerciseViewModel.performedExercise.value!!)
      onCloseItem()
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SpecialistPerfomedExerciseFragmentBinding.inflate(
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

    requireActivity().onBackPressedDispatcher.addCallback(this) {
      goBack()
    }

    binding.apply {
      master = this@SpecialistPerformedExerciseFragment
      viewModel = this@SpecialistPerformedExerciseFragment.exerciseViewModel
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
