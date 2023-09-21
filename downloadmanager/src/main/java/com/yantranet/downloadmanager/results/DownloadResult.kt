package com.yantranet.downloadmanager.results

import com.yantranet.downloadmanager.models.ConfigData

sealed class DownloadResult {
    data class Success(val configData: ConfigData, val downloadLocation: String): DownloadResult()
    data class Failed(val configData: ConfigData, val exception: Exception): DownloadResult()
}