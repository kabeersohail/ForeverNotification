package com.wenable.downloadmanager

import android.content.Context
import com.wenable.downloadmanager.models.ConfigData
import com.wenable.downloadmanager.results.DownloadResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadManager {
    // Create a function to download a file from a given URL.
    suspend fun downloadFile(context: Context, configData: ConfigData): DownloadResult = withContext(Dispatchers.IO) {
        try {
            val internalStorageDirectory = context.filesDir
            val destinationPath = File(internalStorageDirectory, configData.name)
            val inputStream = URL(configData.cdn_path).openStream()
            val outputStream = FileOutputStream(destinationPath)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            DownloadResult.Success(configData, destinationPath.absolutePath)
        } catch (e: Exception) {
            DownloadResult.Failed(configData, e)
        }
    }
}

