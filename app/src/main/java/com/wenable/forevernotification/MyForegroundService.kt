package com.wenable.forevernotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.media.AudioAttributes
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MyForegroundService : Service() {

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationChannelID = "Permanent Notification"
    private var counter = 0

    private val updateNotificationHandler = Handler(Looper.getMainLooper())
    private val updateNotificationRunnable = object : Runnable {
        override fun run() {
            // Update the notification text here
            updateNotificationText("Count: $counter")

            // Increment the counter
            counter++

            // Schedule the next update after 1 second (1000 milliseconds)
            updateNotificationHandler.postDelayed(this, 1000)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_BOOT_COMPLETED) {
            // Service is started from BOOT_COMPLETED broadcast
            notification()
        } else {
            // Service is started from the app
            notification()
        }
        return START_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? = null

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
                .setContentText("Count: 0")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        notificationBuilder?.setContentIntent(pendingIntent)

        // Start the periodic notification update
        updateNotificationHandler.post(updateNotificationRunnable)

        val notification = notificationBuilder?.build()
        startForeground(1, notification)
    }

    private fun updateNotificationText(text: String) {
        // Update the notification text
        notificationBuilder?.setContentText(text)

        // Notify the notification manager to update the notification
        notificationManager.notify(1, notificationBuilder?.build())
    }

    override fun onDestroy() {
        // Remove the updateRunnable when the service is stopped
        updateNotificationHandler.removeCallbacks(updateNotificationRunnable)
        super.onDestroy()
    }
}
