package com.wenable.forevernotification.repository

import android.content.Context
import android.util.Log
import com.wenable.downloadmanager.DownloadManager
import com.wenable.downloadmanager.models.ConfigData
import com.wenable.downloadmanager.results.DownloadResult
import com.wenable.forevernotification.extensions.TAG
import com.wenable.forevernotification.extensions.isConfigDataAlreadyAvailable
import com.wenable.forevernotification.network.ApiProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class DataConfigRepository @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun fetchConfigData() {
        val call = ApiProvider.apiService.getConfigData()
        call.enqueue(object : Callback<List<ConfigData>> {

            override fun onFailure(call: Call<List<ConfigData>>, t: Throwable) {
                Log.e(TAG, "Exception occurred while fetching ConfigData: ${t.message}")
            }

            override fun onResponse(
                call: Call<List<ConfigData>>,
                response: Response<List<ConfigData>>
            ) {
                if (response.isSuccessful) {
                    handleAPISuccess(response)
                } else {
                    handleAPIFailure(response)
                }
            }
        })
    }

    private fun handleAPIFailure(response: Response<List<ConfigData>>) {
        Log.e(TAG, "Error occurred while fetching ConfigData: Error Code: ${response.code()} Error Message: ${response.message()}")
    }

    private fun handleAPISuccess(response: Response<List<ConfigData>>) {
        val configDataList: List<ConfigData> = response.body() ?: run {
            Log.d(TAG, "Response body is null")
            return
        }

        configDataList.forEach { configData ->
            handleConfigData(configData)
        }
    }

    private fun handleConfigData(configData: ConfigData) {

        if (configData.isConfigDataAlreadyAvailable(context.filesDir)) {
            Log.i(TAG, "${configData.name} already downloaded")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            downloadConfigData(configData)
        }
    }

    private suspend fun downloadConfigData(configData: ConfigData) {
        when (val downloadResult = DownloadManager().downloadFile(context, configData)) {
            is DownloadResult.Failed -> {
                // retry mechanism with exponential backoff criteria
                Log.e(TAG, downloadResult.exception.message ?: "Download failed for $downloadResult.")
            }

            is DownloadResult.Success -> {
                // Handle success case, like storing in room database
                downloadResult.configData
            }
        }
    }
}