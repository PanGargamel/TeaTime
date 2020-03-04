package pl.piotrskiba.teatime.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import pl.piotrskiba.teatime.Constants
import pl.piotrskiba.teatime.R

object NotificationUtils {
    @JvmStatic
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            val notificationChannel = NotificationChannel(
                    Constants.TIMER_NOTIFICATION_ID,
                    context.getString(R.string.default_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.vibrationPattern = longArrayOf(0)
            notificationChannel.enableVibration(true)
            notificationChannel.setSound(null, null)
            notificationChannel.importance = NotificationManager.IMPORTANCE_DEFAULT

            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }
}