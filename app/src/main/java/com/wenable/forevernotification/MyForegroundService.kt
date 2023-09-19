package com.wenable.forevernotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.content.IntentFilter
import android.media.AudioAttributes
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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
    private var counter = 0

    private val updateNotificationHandler = Handler(Looper.getMainLooper())
    private val updateNotificationRunnable = object : Runnable {
        override fun run() {
            // Update the notification text here
            updateNotificationText("Count: $counter, Network: ${if (isNetworkAvailable) "Available" else "Unavailable"}")

            // Increment the counter
            counter++

            // Schedule the next update after 1 second (1000 milliseconds)
            updateNotificationHandler.postDelayed(this, 1000)
        }
    }

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Check network availability when network state changes
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            isNetworkAvailable = networkInfo != null && networkInfo.isConnectedOrConnecting

            // Update the notification text to reflect network availability
            updateNotificationText("Count: $counter, Network: ${if (isNetworkAvailable) "Available" else "Unavailable"}")
        }
    }

    private var isNetworkAvailable = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_BOOT_COMPLETED) {
            // Service is started from BOOT_COMPLETED broadcast
            notification()
        } else {
            // Service is started from the app
            notification()
        }

        // Register the network change receiver
        registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        // Start monitoring network state using the flow
        CoroutineScope(Dispatchers.Main).launch {
            networkMonitor.isConnected.collect { isConnected ->
                isNetworkAvailable = isConnected
                updateNotificationText("Count: $counter, Network: ${if (isNetworkAvailable) "Available" else "Unavailable"}")
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        // Remove the updateRunnable when the service is stopped
        updateNotificationHandler.removeCallbacks(updateNotificationRunnable)

        // Unregister the network change receiver
        unregisterReceiver(networkChangeReceiver)

        super.onDestroy()
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
                .setContentText("Count: 0, Network: ${if (isNetworkAvailable) "Available" else "Unavailable"}")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)

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

    override fun onBind(p0: Intent?): IBinder? = null
}
