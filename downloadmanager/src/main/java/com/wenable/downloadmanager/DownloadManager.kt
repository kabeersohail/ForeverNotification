package com.wenable.downloadmanager

import android.content.Context
import androidx.core.content.ContextCompat
import com.wenable.downloadmanager.models.ConfigData
import com.wenable.downloadmanager.results.DownloadResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadManager {

    suspend fun downloadFile(context: Context, configData: ConfigData): DownloadResult = withContext(Dispatchers.IO) {
        try {
            val path = getRootStoragePath(context) + File.separator

            val file = File(path)
            if (!file.exists() && !file.isDirectory) {
                file.mkdirs()
            }

            val destinationPath = File(path, configData.name)
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

    private fun getRootStoragePath(context: Context): String? {
        val externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null)
        val primaryExternalStorage = externalStorageVolumes.first()
        return primaryExternalStorage.absolutePath
    }
}

