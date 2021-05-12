package com.task.igrocery.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://www.mocky.io/")
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}