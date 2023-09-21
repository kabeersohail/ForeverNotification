package com.yantranet.smartagent.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yantranet.smartagent.services.MyForegroundService

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Start your service when the device boots up
            val serviceIntent = Intent(context, MyForegroundService::class.java)
            context?.startService(serviceIntent)
        }
    }
}
