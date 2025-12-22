package com.icefishi.trackesof.hutrjk.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.icefishi.trackesof.IceFishTrackerActivity
import com.icefishi.trackesof.R
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerApplication

private const val ICE_FISH_TRACKER_CHANNEL_ID = "ice_fish_tracker_notifications"
private const val ICE_FISH_TRACKER_CHANNEL_NAME = "IceFishTracker Notifications"
private const val ICE_FISH_TRACKER_NOT_TAG = "IceFishTracker"

class IceFishTrackerPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                iceFishTrackerShowNotification(it.title ?: ICE_FISH_TRACKER_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                iceFishTrackerShowNotification(it.title ?: ICE_FISH_TRACKER_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            iceFishTrackerHandleDataPayload(remoteMessage.data)
        }
    }

    private fun iceFishTrackerShowNotification(title: String, message: String, data: String?) {
        val iceFishTrackerNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ICE_FISH_TRACKER_CHANNEL_ID,
                ICE_FISH_TRACKER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            iceFishTrackerNotificationManager.createNotificationChannel(channel)
        }

        val iceFishTrackerIntent = Intent(this, IceFishTrackerActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val iceFishTrackerPendingIntent = PendingIntent.getActivity(
            this,
            0,
            iceFishTrackerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val iceFishTrackerNotification = NotificationCompat.Builder(this, ICE_FISH_TRACKER_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ice_fish_tracker_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(iceFishTrackerPendingIntent)
            .build()

        iceFishTrackerNotificationManager.notify(System.currentTimeMillis().toInt(), iceFishTrackerNotification)
    }

    private fun iceFishTrackerHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}