package el.ka.speechart.other

import android.app.Activity
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import el.ka.speechart.view.dialog.AddExerciseDialog

class FilePicker(val fragment: Fragment, val listener: Listener, val type: String) {

  private val intent by lazy {
    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = type

    return@lazy intent
  }

  fun pickup() {
    pickupLauncher.launch(intent)
  }

  private val pickupLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (it.resultCode == Activity.RESULT_OK) {
      val uri: Uri? = it.data?.data
      listener.onPicked(uri)
    }
  }

  fun getMediaFileInfo(uri: Uri): MediaFileInfo {
    val mediaFileInfo = MediaFileInfo(uri)

    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(fragment.requireContext(), uri)
    val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    mediaFileInfo.duration = (durationStr?.toInt()?.div(1000)) ?: 0

    val cursor = fragment.requireContext().contentResolver.query(
      uri, arrayOf(MediaStore.Audio.Media.DISPLAY_NAME),
      null, null, null
    )

    if (cursor != null) {
      cursor.moveToFirst()
      val titleIdx = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
      val title = cursor.getString(titleIdx)
      mediaFileInfo.title = title ?: "null"
      cursor.close()
    }
    return mediaFileInfo
  }

  companion object {
    interface Listener {
      fun onPicked(uri: Uri?)
    }
  }

}