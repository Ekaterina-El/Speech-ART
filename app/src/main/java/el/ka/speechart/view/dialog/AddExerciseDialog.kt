package el.ka.speechart.view.dialog

import android.app.Dialog
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import el.ka.speechart.R
import el.ka.speechart.databinding.AddExerciseDialogBinding
import el.ka.speechart.other.*
import el.ka.speechart.service.model.Exercise
import el.ka.speechart.view.ui.BaseFragment

class AddExerciseDialog(
  context: Context,
  val fragment: BaseFragment,
  private val filePicker: FilePicker,
  private val listener: Listener
) : Dialog(context) {
  private lateinit var binding: AddExerciseDialogBinding
  private var levelOfDifficulty: LevelOfDifficulty = LevelOfDifficulty.EASY
  private var fileUrl: Uri? = null

  fun setPickedFile(uri: Uri?) {
    fileUrl = uri

    val mediaFileInfo = when (uri) {
      null -> null
      else -> filePicker.getMediaFileInfo(uri)
    }

    binding.fileName.text = when (mediaFileInfo) {
      null -> context.getString(R.string.file_no_picked)
      else -> "${mediaFileInfo.title} | ${mediaFileInfo.duration.toMinutesAndSeconds()}"
    }
  }

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = AddExerciseDialogBinding.inflate(LayoutInflater.from(context))
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)

    fragment.createSpinner(
      R.array.levelTypes, LevelOfDifficulty.values().toList(),
      binding.spinner, LevelOfDifficulty.EASY
    )

    binding.spinner.addListener {
      levelOfDifficulty = (it as SpinnerItem).value as LevelOfDifficulty
    }

    binding.layoutFile.setOnClickListener {
      filePicker.pickup()
    }

    binding.buttonContinue.setOnClickListener {
      val name = binding.name.text.toString()
      val description = binding.description.text.toString()
      val text = binding.text.text.toString()

      val exercise = Exercise(
        name = name, description = description, text = text, levelOfDifficulty = levelOfDifficulty,
        referencePronunciationFileUrl = ""
      )

      if (checkFields(name, description, text, fileUrl)) {
        listener.onSave(exercise)
        close()
      }
    }
  }

  private fun checkFields(name: String, description: String, text: String, fileUrl: Uri?): Boolean {
    binding.layoutName.error = ""
    binding.layoutDescription.error = ""
    binding.layoutText.error = ""
    binding.fileError.text = ""

    var errors = 0
    val isRequire = context.getString(R.string.is_require)
    if (name.isEmpty()) {
      binding.layoutName.error = isRequire
      errors++
    }
    if (description.isEmpty()) {
      binding.layoutDescription.error = isRequire
      errors++
    }
    if (text.isEmpty()) {
      binding.layoutText.error = isRequire
      errors++
    }
    if (fileUrl == null) {
      binding.fileError.text = isRequire
      errors++
    }

    return errors == 0
  }

  fun open() {
    show()
  }

  private fun close() {
    binding.name.setText("")
    binding.description.setText("")
    binding.text.setText("")
    setPickedFile(null)
    dismiss()
  }

  companion object {
    interface Listener {
      fun onSave(exercise: Exercise)
    }
  }
}