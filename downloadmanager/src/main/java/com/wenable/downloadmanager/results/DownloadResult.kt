package com.wenable.downloadmanager.results

import com.wenable.downloadmanager.models.ConfigData

sealed class DownloadResult {
    data class Success(val configData: ConfigData, val downloadLocation: String): DownloadResult()
    data class Failed(val configData: ConfigData, val exception: Exception): DownloadResult()
}