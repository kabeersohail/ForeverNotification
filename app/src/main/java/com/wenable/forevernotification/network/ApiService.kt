package com.wenable.forevernotification.network

import com.wenable.downloadmanager.models.ConfigData
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("fetch_config")
    fun getConfigData(): Call<List<ConfigData>>
}
