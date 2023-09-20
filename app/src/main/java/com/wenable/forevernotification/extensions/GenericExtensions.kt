package com.wenable.forevernotification.extensions

import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import com.wenable.downloadmanager.models.ConfigData
import java.io.File

fun ConnectivityManager.isConnected(): Boolean = getNetworkCapabilities(activeNetwork)?.hasCapability(NET_CAPABILITY_INTERNET) == true

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if(tag.length <= 27) tag else tag.substring(0, 23)
    }

fun ConfigData.isConfigDataAlreadyAvailable(filesDir: File): Boolean {
    val filePath = File(filesDir, name)
    return filePath.exists() && filePath.length() == sizeInBytes
}