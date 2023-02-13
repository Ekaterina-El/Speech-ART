package el.ka.speechart.service.androidServices

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.NotificationUtil.IMPORTANCE_HIGH
import el.ka.speechart.R

class PlayerService : Service() {

  // player
  lateinit var player: ExoPlayer
  lateinit var notificationManager: PlayerNotificationManager

  override fun onBind(p0: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()

    player = ExoPlayer.Builder(applicationContext).build()

    val audioAttribute = AudioAttributes.Builder()
      .setUsage(C.USAGE_MEDIA)
      .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
      .build()
    player.setAudioAttributes(audioAttribute, true)

    val channelId = resources.getString(R.string.app_name) + " Music Channel "
    val notificationId = 10110
    notificationManager = PlayerNotificationManager.Builder(this, notificationId, channelId)
      .setMediaDescriptionAdapter(descriptionAdapter)
      .setChannelImportance(IMPORTANCE_HIGH)
      .setSmallIconResourceId(R.drawable.logo_icon)
      .setChannelDescriptionResourceId(R.string.app_name)
      .setPauseActionIconResourceId(R.drawable.ic_pause)
      .setPlayActionIconResourceId(R.drawable.ic_play)
      .setChannelNameResourceId(R.string.app_name)
      .build()

    notificationManager.setPlayer(player)
    notificationManager.setPriority(NotificationCompat.PRIORITY_MAX)
    notificationManager.setUseRewindAction(false)
    notificationManager.setUseFastForwardAction(false)
  }
  
  val notificationListener = object: PlayerNotificationManager.NotificationListener {
    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
      super.onNotificationCancelled(notificationId, dismissedByUser)
//      stopForeground(this)
      if (player.isPlaying) {
        player.pause()
      }
    }
  }


  val descriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence {
      return player.currentMediaItem?.mediaMetadata?.title ?: ""
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? = null
    override fun getCurrentContentText(player: Player): CharSequence? = null
    override fun getCurrentLargeIcon(
      player: Player,
      callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? = null

  }
}