package el.ka.speechart.other

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions

class ImageChanger(fragment: Fragment) {
  private var cropOptions: CropImageOptions? = null
  private var listener: ((Uri) -> Unit)? = null

  fun change(cropOptions: CropImageOptions, listener: (Uri) -> Unit) {
    this.cropOptions = cropOptions
    this.listener = listener

    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    imagePickerLauncher.launch(intent)
  }

  private val imagePickerLauncher =
    fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        val uri: Uri? = result.data?.data
        cropOptions?.let { cropImageLauncher.launch(CropImageContractOptions(uri, it)) }
      }
    }

  private val cropImageLauncher =
    fragment.registerForActivityResult(CropImageContract()) { result ->
      if (!result.isSuccessful || result.uriContent == null) return@registerForActivityResult

      val uri = result.uriContent!!
      listener?.let { it(uri) }
    }
}