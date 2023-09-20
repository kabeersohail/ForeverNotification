package com.wenable.forevernotification.application

import android.app.Application
import android.content.Intent
import com.wenable.forevernotification.services.MyForegroundService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ForeverNotificationApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Start the MyForegroundService
        startService(Intent(this, MyForegroundService::class.java))
    }
}