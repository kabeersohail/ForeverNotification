package com.wenable.downloadmanager

import android.content.Context
import com.wenable.downloadmanager.models.ConfigData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadManager {
    // Create a function to download a file from a given URL.
    suspend fun downloadFile(context: Context, configData: ConfigData) = withContext(Dispatchers.IO) {
        val internalStorageDirectory = context.filesDir
        val destinationPath = File(internalStorageDirectory, configData.name)
        val inputStream = URL("https://www.pexels.com/download/video/3209828/").openStream()
        val outputStream = FileOutputStream(destinationPath)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }
}
