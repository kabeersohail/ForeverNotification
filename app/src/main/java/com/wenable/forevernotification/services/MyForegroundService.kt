package com.wenable.forevernotification.services

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
import com.wenable.forevernotification.R
import com.wenable.forevernotification.repository.DataConfigRepository
import com.wenable.forevernotification.utils.NetworkMonitor
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

    @Inject
    lateinit var dataConfigRepository: DataConfigRepository

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

        if(networkMonitor.isConnected) {
            dataConfigRepository.fetchConfigData()
        }

        // Start monitoring network state using the flow
        monitorNetwork()

        return START_STICKY
    }

    private fun monitorNetwork() {
        CoroutineScope(Dispatchers.Main).launch {
            networkMonitor.isConnectedFlow.collect { isConnected ->
                updateNotificationText("Network: ${if (isConnected) "Available" else "Unavailable"}")

                if (isConnected) {
                    dataConfigRepository.fetchConfigData()
                }
            }
        }
    }

    private fun notification() {
        val channel = NotificationChannel(
            notificationChannelID,
            "notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        channel.setSound(Uri.EMPTY, audioAttributes)

        Toast.makeText(applicationContext, "Service Started", Toast.LENGTH_SHORT).show()

        notificationManager.createNotificationChannel(channel)

        notificationBuilder =
            NotificationCompat.Builder(this, notificationChannelID)
                .setContentTitle("SmartAgent")
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
