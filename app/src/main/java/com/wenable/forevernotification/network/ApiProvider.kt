package com.wenable.forevernotification.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    private const val BASE_URL = "https://demo4482685.mockable.io/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}