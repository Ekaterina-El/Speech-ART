package el.ka.speechart.view.ui.core.specialist

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
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
import java.util.*

class AddExerciseFragment(val onGoBack: () -> Unit) : UserBaseFragment() {
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

    if (errors == null) return
    for (error in errors) {
      val errorText = getString(error.errorType!!.messageRes)
      when (error.field) {
        Field.NAME -> binding.layoutName.error = errorText
        Field.DESCRIPTION -> binding.layoutDescription.error = errorText
        Field.EXERCISE_TEXT -> binding.layoutText.error = errorText
        else -> Unit
      }
    }
  }

  override val externalActionObserver = Observer<Action?> {
    if (it == Action.GO_BACK) {
      val exercise = addExerciseViewModel.addedExercise
      if (exercise != null) specialistViewModel.addLocalExercise(exercise)
      goBack()
    }
  }
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
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
  }

  override fun onResume() {
    super.onResume()
    addExerciseViewModel.fieldErrors.observe(viewLifecycleOwner, fieldErrorsObserver)
    addExerciseViewModel.work.observe(viewLifecycleOwner, workObserver)
    addExerciseViewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
    addExerciseViewModel.error.observe(viewLifecycleOwner, errorObserver)
  }

  override fun onStart() {
    super.onStart()
    addExerciseViewModel.fieldErrors.removeObserver(fieldErrorsObserver)
    addExerciseViewModel.work.removeObserver(workObserver)
    addExerciseViewModel.externalAction.removeObserver(externalActionObserver)
    addExerciseViewModel.error.removeObserver(errorObserver)

  }

  fun goBack() {
    if (mediaRecorder != null) cancelRecord()
    destroyRefMediaPlayer()
    addExerciseViewModel.clearFields()
    onGoBack()
  }

  private fun cancelRecord() {
    binding.recordVisualizer.release()
    deleteRecord(withStopRecord = true)
  }

  private val requestPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
      for (permission in permissions) {
        if (permission.key == recordAudioPermission) {
          when (permission.value) {
            true -> startRecord()
            false -> {
              showInformDialog(
                getString(R.string.permission_for_record_title),
                getString(R.string.permission_for_record_description),
                getString(R.string.permission_for_record_warning)
              ) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
              }
            }
          }
        }
      }
    }

  private var mediaRecorder: MediaRecorder? = null
  private var handler = Handler(Looper.getMainLooper())

  fun startRecord() {
    // check permission
    if (!isRecordAudioPermissionGranted) {
      toast(getString(R.string.for_record_audio_we_need_permission))
      requestPermissionLauncher.launch(arrayOf(recordAudioPermission))
      return
    }

    val name = Calendar.getInstance().time.toString().split(" ").joinToString("-")
    val output =
      requireContext().getExternalFilesDir(Context.STORAGE_SERVICE)?.absolutePath + "/$name.mp3"
    addExerciseViewModel.setUserFileUrl(output)
    mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      MediaRecorder(requireContext())
    } else {
      MediaRecorder()
    }
    mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
    mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
    mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    mediaRecorder!!.setOutputFile(output)

    binding.recordVisualizer.prepare()

    mediaRecorder!!.prepare()
    mediaRecorder!!.start()

    addExerciseViewModel.setMusicStatus(Status.RECORDING)
    handler.post(recorderUpdate)
  }

  private val recordDelta = 40L
  private val recorderUpdate: Runnable by lazy {
    Runnable {
      if (addExerciseViewModel.musicStatus.value == Status.RECORDING && mediaRecorder != null) {
        binding.recordVisualizer.addAmplitude(mediaRecorder!!.maxAmplitude)
        addExerciseViewModel.setCurrentRecordTime(addExerciseViewModel.currentRecordTime.value!! + recordDelta.toInt())
        handler.postDelayed(recorderUpdate, recordDelta)
      }
    }
  }

  fun togglePauseRecord() {
    if (addExerciseViewModel.musicStatus.value == Status.PAUSED_RECORDING) {
      mediaRecorder!!.resume()
      addExerciseViewModel.setMusicStatus(Status.RECORDING)
      handler.post(recorderUpdate)
    } else {
      mediaRecorder!!.pause()
      addExerciseViewModel.setMusicStatus(Status.PAUSED_RECORDING)
    }
  }

  fun stopRecord() {
    addExerciseViewModel.setMusicStatus(Status.RECORDED)
    mediaRecorder!!.stop()
    mediaRecorder!!.release()
    addExerciseViewModel.setAudioWave(binding.recordVisualizer.getWave())
    addExerciseViewModel.uploadAudioFile()
  }

  private var mediaRefPlayer: MediaPlayer? = null
  fun playPauseMusic() {
    if (addExerciseViewModel.musicStatus.value == Status.RECORDING) return

    val musicStatus = addExerciseViewModel.musicStatus.value!!
    if (musicStatus == Status.PAUSED || musicStatus == Status.NO_LOADED || musicStatus == Status.RECORDED) {
      if (mediaRefPlayer == null) {
        val url = addExerciseViewModel.fileUrl.value ?: return
        if (addExerciseViewModel.preparedRefFileUrl.value == url) return
        prepareRefMusicPlayer(url)
        return
      } else {
        mediaRefPlayer!!.start()
        updateRefSeekBar()
      }
    } else if (musicStatus == Status.PLAYING) {
      handler.removeCallbacks(refUpdater)
      mediaRefPlayer!!.pause()
    }
    addExerciseViewModel.setMusicStatus(if (musicStatus == Status.PLAYING) Status.PAUSED else Status.PLAYING)
  }

  private fun prepareRefMusicPlayer(url: String) {
    try {
      addExerciseViewModel.setPreparedRefFileUrl(url)
      mediaRefPlayer = MediaPlayer()
      mediaRefPlayer!!.setDataSource(url)
      addExerciseViewModel.setMusicStatus(Status.LOADING)

      mediaRefPlayer!!.setOnPreparedListener {
        addExerciseViewModel.setMusicDuration(mediaRefPlayer!!.duration / 1000)
        addExerciseViewModel.setMusicStatus(Status.PLAYING)
        mediaRefPlayer!!.start()
        updateRefSeekBar()
      }
      mediaRefPlayer!!.prepareAsync()

      mediaRefPlayer!!.setOnCompletionListener {
        addExerciseViewModel.setMusicStatus(Status.PAUSED)
      }
    } catch (e: Exception) {
      toast(e.message ?: requireContext().getString(Errors.unknown.messageRes))
    }
  }

  private fun updateRefSeekBar() {
    if (mediaRefPlayer == null) return

    binding.refSeekBar.progress = mediaRefPlayer!!.progress
    if (mediaRefPlayer!!.isPlaying) {
      handler.postDelayed(refUpdater, 1000)
    }
  }

  private val refUpdater by lazy {
    Runnable {
      if (mediaRefPlayer == null) return@Runnable

      updateRefSeekBar()
      addExerciseViewModel.setCurrentMusicTime(mediaRefPlayer!!.currentPosition / 1000)
    }
  }

  private fun destroyRefMediaPlayer() {
    mediaRefPlayer?.release()
    mediaRefPlayer = null
    addExerciseViewModel.setPreparedRefFileUrl(null)
  }

  fun deleteRecord(withStopRecord: Boolean = false) {
    if (withStopRecord) {
      try {
        mediaRecorder?.stop()
        mediaRecorder?.release()
      } catch (_: java.lang.IllegalStateException) {

      }
    }

    if (mediaRefPlayer != null) {
      mediaRefPlayer!!.pause()
      mediaRefPlayer!!.release()
      mediaRecorder = null
    }

    addExerciseViewModel.clearFields()
  }


}