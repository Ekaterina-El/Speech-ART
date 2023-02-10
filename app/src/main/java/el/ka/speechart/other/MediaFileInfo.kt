package el.ka.speechart.other

import android.net.Uri

data class MediaFileInfo(
  val uri: Uri? = null,
  var title: String = "",
  var duration: Int = 0,
)