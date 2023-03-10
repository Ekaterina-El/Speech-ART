package el.ka.speechart.view.ui.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import el.ka.speechart.R
import el.ka.speechart.other.*
import el.ka.speechart.view.customView.WaveFormView
import el.ka.speechart.view.ui.BaseFragment
import el.ka.speechart.viewModel.ExerciseViewModel
import java.util.*


abstract class ExerciseBaseFragment(val onCloseItem: () -> Unit) : BaseFragment() {
  private val handler by lazy { Handler() }
  val exerciseViewModel: ExerciseViewModel by activityViewModels()

  private fun destroyMediaPlayer() {
    destroyMediaReferencePronunciationPlayer()
    destroyMediaUserPlayer()
  }

  override fun onResume() {
    super.onResume()
    if (exerciseViewModel.musicStatus.value == Status.PLAYING) mediaPlayer!!.start()
    if (exerciseViewModel.userMusicStatus.value == Status.PLAYING) userMediaPlayer!!.start()
  }

  override fun onDestroy() {
    super.onDestroy()
    destroyMediaPlayer()
  }

  override fun onStop() {
    super.onStop()
    if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
    if (userMediaPlayer?.isPlaying == true) userMediaPlayer?.pause()
  }

  fun goBack() {
    if (mediaRecorder != null) cancelRecord()
    rewindAudio(0, true)
    rewindUserAudio(0, true)
    destroyMediaPlayer()
    exerciseViewModel.clearUserData()
    exerciseViewModel.setMusicStatus(Status.NO_LOADED)
    onCloseItem()
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    if (hidden) goBack()
  }


  fun sendToCheck() {
    if (userMediaPlayer != null) {
      userMediaPlayer!!.pause()
      userMediaPlayer!!.release()
      userMediaPlayer = null
    }
    exerciseViewModel.sendToCheck()
  }

  // region Music Player 1
  private var mediaPlayer: MediaPlayer? = null
  abstract var seekBar: SeekBar

  private fun updateSeekBar() {
    if (mediaPlayer == null) return

    seekBar.progress = mediaPlayer!!.progress
    if (mediaPlayer!!.isPlaying) {
      handler.postDelayed(updater, 1000)
    }
  }

  private val updater by lazy {
    Runnable {
      if (mediaPlayer == null) return@Runnable

      updateSeekBar()
      exerciseViewModel.setCurrentMusicTime(mediaPlayer!!.currentPosition)
    }
  }

  fun rewindAudio(progress: Int, setNull: Boolean = false) {
    mediaPlayer?.playAt(progress)
    exerciseViewModel.setCurrentMusicTime(mediaPlayer?.currentPosition ?: 0)
    if (setNull) seekBar.progress = 0
  }

  fun rewindUserAudio(progress: Int, setNull: Boolean = false) {
    userMediaPlayer?.playAt(progress)
    exerciseViewModel.setCurrentUserMusicTime(userMediaPlayer?.currentPosition ?: 0)
    if (setNull) userSeekBar?.progress = 0
  }

  private fun getAudioFileUrl(): String? =
    exerciseViewModel.exercise.value?.referencePronunciationUrl


  private fun prepareMusicPlayer(url: String) {
    try {
      exerciseViewModel.setPrepareFileUrl(url)

      mediaPlayer = MediaPlayer()
      mediaPlayer!!.setDataSource(url)
      exerciseViewModel.setMusicStatus(Status.LOADING)
      mediaPlayer!!.setOnPreparedListener {
        exerciseViewModel.setMusicDuration(mediaPlayer!!.duration)
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
    Log.d(
      "playPauseMusic",
      "musicStatus: $musicStatus | mediaPlayer is null: ${mediaPlayer == null}"
    )

    if (musicStatus == Status.PAUSED || musicStatus == Status.NO_LOADED) {
      if (userMediaPlayer?.isPlaying == true) playPauseUserMusic()

      if (mediaPlayer == null || musicStatus == Status.NO_LOADED) {
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

  private fun destroyMediaReferencePronunciationPlayer() {
    mediaPlayer?.release()
    mediaPlayer = null
    exerciseViewModel.setPrepareFileUrl(null)
  }
  // endregion

  // region Media User Player
  abstract var userSeekBar: SeekBar?
  private var userMediaPlayer: MediaPlayer? = null

  fun playPauseUserMusic() {
    if (exerciseViewModel.userMusicStatus.value == Status.RECORDING) return

    val musicStatus = exerciseViewModel.userMusicStatus.value!!
    if (musicStatus == Status.PAUSED || musicStatus == Status.NO_LOADED || musicStatus == Status.RECORDED) {
      if (mediaPlayer?.isPlaying == true) playPauseMusic()
      if (userMediaPlayer == null) {
        val url = exerciseViewModel.userFileUrl.value ?: return
        if (exerciseViewModel.preparedUserFileUrl.value == url) return
        prepareUserMusicPlayer(url)
        return
      } else {
        userMediaPlayer!!.start()
        updateUserSeekBar()
      }
    } else if (musicStatus == Status.PLAYING) {
      handler.removeCallbacks(updater)
      userMediaPlayer!!.pause()
    }
    exerciseViewModel.setUserMusicStatus(if (musicStatus == Status.PLAYING) Status.PAUSED else Status.PLAYING)
  }

  private fun prepareUserMusicPlayer(url: String) {
    try {
      exerciseViewModel.setPrepareUserFileUrl(url)
      userMediaPlayer = MediaPlayer()
      userMediaPlayer!!.setDataSource(url)
      exerciseViewModel.setUserMusicStatus(Status.LOADING)

      userMediaPlayer!!.setOnPreparedListener {
        exerciseViewModel.setUserMusicDuration(userMediaPlayer!!.duration)
        exerciseViewModel.setUserMusicStatus(Status.PLAYING)
        userMediaPlayer!!.start()
        updateUserSeekBar()
      }
      userMediaPlayer!!.prepareAsync()


      userMediaPlayer!!.setOnCompletionListener {
        exerciseViewModel.setUserMusicStatus(Status.PAUSED)
      }
    } catch (e: Exception) {
      toast(e.message ?: requireContext().getString(Errors.unknown.messageRes))
    }
  }

  private fun updateUserSeekBar() {
    if (userMediaPlayer == null) return

    userSeekBar!!.progress = userMediaPlayer?.progress ?: 0
    if (userMediaPlayer!!.isPlaying) {
      handler.postDelayed(userUpdater, 1000)
    }
  }

  private val userUpdater by lazy {
    Runnable {
      if (userMediaPlayer == null) return@Runnable

      updateUserSeekBar()
      exerciseViewModel.setCurrentUserMusicTime(userMediaPlayer!!.currentPosition)
    }
  }

  private fun destroyMediaUserPlayer() {
    userMediaPlayer?.release()
    userMediaPlayer = null
    exerciseViewModel.setPrepareUserFileUrl(null)
  }
  // endregion

  // region Recorder
  private var mediaRecorder: MediaRecorder? = null
  open var userRecordVisualizer: WaveFormView? = null

  private val recordDelta = 40L

  private val recorderUpdate: Runnable by lazy {
    Runnable {
      if (exerciseViewModel.userMusicStatus.value == Status.RECORDING && mediaRecorder != null) {
        userRecordVisualizer?.addAmplitude(mediaRecorder!!.maxAmplitude)
        exerciseViewModel.setCurrentUserRecordTime(exerciseViewModel.currentUserRecordTime.value!! + recordDelta.toInt())
        handler.postDelayed(recorderUpdate, recordDelta)
      }
    }
  }

  private fun cancelRecord() {
    userRecordVisualizer?.release()
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

  fun startRecord() {
    // check permission
    if (!isRecordAudioPermissionGranted) {
      toast(getString(R.string.for_record_audio_we_need_permission))
      requestPermissionLauncher.launch(arrayOf(recordAudioPermission))
      return
    }

    if (mediaPlayer?.isPlaying == true) playPauseMusic()

    val name = Calendar.getInstance().time.toString().split(" ").joinToString("-")
    val output =
      requireContext().getExternalFilesDir(Context.STORAGE_SERVICE)?.absolutePath + "/$name.mp3"
    exerciseViewModel.setUserFileUrl(output)
    mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      MediaRecorder(requireContext())
    } else {
      MediaRecorder()
    }
    mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
    mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
    mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    mediaRecorder!!.setOutputFile(output)

    userRecordVisualizer!!.prepare()

    mediaRecorder!!.prepare()
    mediaRecorder!!.start()

    exerciseViewModel.setUserMusicStatus(Status.RECORDING)
    handler.post(recorderUpdate)
  }

  fun stopRecord() {
    exerciseViewModel.setUserMusicStatus(Status.RECORDED)
    mediaRecorder!!.stop()
    mediaRecorder!!.release()
    if (userRecordVisualizer != null) {
      exerciseViewModel.setUserWave(userRecordVisualizer!!.getWave())
    }
    exerciseViewModel.uploadAudioFile()
  }

  fun togglePauseRecord() {
    if (exerciseViewModel.userMusicStatus.value == Status.PAUSED_RECORDING) {
      mediaRecorder!!.resume()
      exerciseViewModel.setUserMusicStatus(Status.RECORDING)
      handler.post(recorderUpdate)
    } else {
      mediaRecorder!!.pause()
      exerciseViewModel.setUserMusicStatus(Status.PAUSED_RECORDING)
    }
  }

  fun deleteRecord(withStopRecord: Boolean = false) {
    if (withStopRecord) {
      try {
        mediaRecorder?.stop()
        mediaRecorder?.release()
      } catch (_: java.lang.IllegalStateException) {

      }
    }

    if (userMediaPlayer != null) {
      userMediaPlayer!!.pause()
      userMediaPlayer!!.release()
      userMediaPlayer = null
    }

    exerciseViewModel.clearUserData()
  }
  // endregion
}