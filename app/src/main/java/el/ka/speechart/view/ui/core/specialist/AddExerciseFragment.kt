package el.ka.speechart.view.ui.core.specialist

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import el.ka.speechart.R
import el.ka.speechart.databinding.AddExerciseFragmentBinding
import el.ka.speechart.other.*
import el.ka.speechart.view.ui.UserBaseFragment
import el.ka.speechart.viewModel.AddExerciseViewModel
import el.ka.speechart.viewModel.SpecialistViewModel
import el.ka.speechart.viewModel.UserViewModel

class AddExerciseFragment(val onGoBack: () -> Unit): UserBaseFragment() {
  override val userViewModel: UserViewModel by activityViewModels()
  val specialistViewModel: SpecialistViewModel by activityViewModels()

  private lateinit var binding: AddExerciseFragmentBinding
  private lateinit var addExerciseViewModel: AddExerciseViewModel

  private val fieldErrorsObserver = Observer<List<FieldError>> {
    showErrors(it)
  }

  private fun showErrors(errors: List<FieldError>?) {
    binding.layoutName.error = ""
    binding.layoutDescription.error = ""
    binding.layoutText.error = ""
    binding.fileError.text = ""

    if (errors == null) return
    for (error in errors) {
      val errorText = getString(error.errorType!!.messageRes)
      when (error.field) {
        Field.NAME -> binding.layoutName.error = errorText
        Field.DESCRIPTION -> binding.layoutDescription.error = errorText
        Field.EXERCISE_TEXT -> binding.layoutText.error = errorText
        Field.MEDIA_FILE -> binding.fileError.text = errorText
        else -> Unit
      }
    }
  }

  override val externalActionObserver = Observer<Action?>{
    if (it == Action.GO_BACK) {
      val exercise = addExerciseViewModel.addedExercise
      if (exercise != null) specialistViewModel.addLocalExercise(exercise)
      goBack()
    }
  }

  private lateinit var filePicker: FilePicker
  private fun initFilePicker() {
    val listener = object : FilePicker.Companion.Listener {
      override fun onPicked(uri: Uri?) {
        val mediaDataInfo = if (uri != null) filePicker.getMediaFileInfo(uri) else null
        addExerciseViewModel.setPickedFile(mediaDataInfo)
      }
    }
    filePicker = FilePicker(this, listener, Constants.pickUpAudioType)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    initFilePicker()
    addExerciseViewModel = ViewModelProvider(this)[AddExerciseViewModel::class.java]

    binding = AddExerciseFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@AddExerciseFragment
      addExerciseViewModel = this@AddExerciseFragment.addExerciseViewModel
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    createSpinner(
      R.array.levelTypes, LevelOfDifficulty.values().toList(),
      binding.spinner, LevelOfDifficulty.EASY
    )

    binding.spinner.addListener {
      val levelOfDifficulty = (it as SpinnerItem).value as LevelOfDifficulty
      addExerciseViewModel.setLevelOfDifficulty(levelOfDifficulty)
    }

    binding.layoutFile.setOnClickListener {
      filePicker.pickup()
    }

    binding.buttonContinue.setOnClickListener {
      addExerciseViewModel.addExercise()
    }
  }

  override fun onResume() {
    super.onResume()
    addExerciseViewModel.fieldErrors.observe(viewLifecycleOwner, fieldErrorsObserver)
    addExerciseViewModel.work.observe(viewLifecycleOwner, workObserver)
    addExerciseViewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
  }

  override fun onStart() {
    super.onStart()
    addExerciseViewModel.fieldErrors.removeObserver(fieldErrorsObserver)
    addExerciseViewModel.work.removeObserver(workObserver)
    addExerciseViewModel.externalAction.removeObserver(externalActionObserver)
  }

  fun goBack() {
    addExerciseViewModel.clearFields()
    onGoBack()
  }
}