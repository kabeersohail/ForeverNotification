package com.wenable.forevernotification.extensions

import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET

fun ConnectivityManager.isConnected(): Boolean = getNetworkCapabilities(activeNetwork)?.hasCapability(NET_CAPABILITY_INTERNET) == true
