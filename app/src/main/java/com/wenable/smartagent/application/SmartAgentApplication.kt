package com.wenable.smartagent.application

import android.app.Application
import android.content.Intent
import com.wenable.smartagent.services.MyForegroundService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartAgentApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Start the MyForegroundService
        startService(Intent(this, MyForegroundService::class.java))
    }
}