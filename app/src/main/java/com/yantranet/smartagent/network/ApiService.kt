package com.yantranet.smartagent.network

import com.yantranet.downloadmanager.models.ConfigData
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("fetch_config")
    fun getConfigData(): Call<List<ConfigData>>
}
