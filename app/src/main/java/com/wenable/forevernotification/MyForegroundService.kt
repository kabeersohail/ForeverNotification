package com.wenable.forevernotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.media.AudioAttributes
import android.net.Uri
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyForegroundService : Service() {

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationChannelID = "Permanent Notification"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_BOOT_COMPLETED) {
            // Service is started from BOOT_COMPLETED broadcast
            notification()
        } else {
            // Service is started from the app
            notification()
        }

        // Start monitoring network state using the flow
        CoroutineScope(Dispatchers.Main).launch {
            networkMonitor.isConnected.collect { isConnected ->
                updateNotificationText("Network: ${if (isConnected) "Available" else "Unavailable"}")
            }
        }

        return START_STICKY
    }

    private fun notification() {
        val channel = NotificationChannel(
            notificationChannelID,
            "notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        // Set sound to null to make the notification silent
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        channel.setSound(Uri.EMPTY, audioAttributes)

        Toast.makeText(applicationContext, "Service Started", Toast.LENGTH_SHORT).show()

        notificationManager.createNotificationChannel(channel)

        notificationBuilder =
            NotificationCompat.Builder(this, notificationChannelID)
                .setContentTitle("Weather")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)

        val notification = notificationBuilder?.build()
        startForeground(1, notification)
    }

    private fun updateNotificationText(text: String) {
        // Update the notification text
        notificationBuilder?.setContentText(text)

        // Notify the notification manager to update the notification
        notificationManager.notify(1, notificationBuilder?.build())
    }

    override fun onBind(p0: Intent?): IBinder? = null
}
