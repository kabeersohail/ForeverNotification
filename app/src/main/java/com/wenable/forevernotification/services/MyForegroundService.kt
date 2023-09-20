package com.wenable.forevernotification.services

import DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.media.AudioAttributes
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.wenable.forevernotification.utils.NetworkMonitor
import com.wenable.forevernotification.R
import com.wenable.forevernotification.extensions.TAG
import com.wenable.forevernotification.models.ConfigData
import com.wenable.forevernotification.network.ApiProvider
import com.wenable.forevernotification.utils.Constants.HTML
import com.wenable.forevernotification.utils.Constants.IMAGE
import com.wenable.forevernotification.utils.Constants.VIDEO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

                if(isConnected) {
                    val call = ApiProvider.apiService.getConfigData()

                    call.enqueue(object : Callback<List<ConfigData>> {

                        override fun onFailure(call: Call<List<ConfigData>>, t: Throwable) {
                            Log.e(TAG, "Exception occurred while fetching ConfigData: ${t.message}")
                        }

                        override fun onResponse(
                            call: Call<List<ConfigData>>,
                            response: Response<List<ConfigData>>
                        ) {
                            if (response.isSuccessful) {
                                val configDataList: List<ConfigData> = response.body() ?: return

                                configDataList.forEach { configData ->
                                    when(configData.type) {
                                        IMAGE -> {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                DownloadManager().downloadAndSaveImageToGallery(
                                                    this@MyForegroundService,
                                                    configData.cdn_path,
                                                    "YantraNet"
                                                )
                                            }
                                        }
                                        VIDEO -> {}
                                        HTML -> {}
                                    }
                                }

                                // Use the configData object as needed
                            } else {
                                Log.e(TAG, "Error occurred while fetching ConfigData: Error Code: ${response.code()} Error Message: ${response.message()}")
                            }
                        }
                    })
                }

            }
        }

        return START_STICKY
    }

    private suspend fun exampleImageDownload() {
        DownloadManager().downloadAndSaveImageToGallery(
            this@MyForegroundService,
            "https://images.pexels.com/photos/842711/pexels-photo-842711.jpeg",
            "MyAlbus"
        )
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
