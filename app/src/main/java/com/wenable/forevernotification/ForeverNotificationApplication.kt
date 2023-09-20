package com.wenable.forevernotification

import android.app.Application
import android.content.Intent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ForeverNotificationApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Start the MyForegroundService
        startService(Intent(this, MyForegroundService::class.java))
    }
}