package com.wenable.forevernotification

import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET

object ConnectedCompat {
    fun isConnected(connectivityManager: ConnectivityManager): Boolean =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        ?.hasCapability(NET_CAPABILITY_INTERNET) == true
}