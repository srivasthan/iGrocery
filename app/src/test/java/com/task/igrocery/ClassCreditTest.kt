package com.task.igrocery

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.task.igrocery.Model.GroceryList
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


class AuthenticationManager(endpoint: String) {
  //  var login: LoginResponse? = null

    private val okHttpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.mocky.io/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

   // private val api = retrofit.create(LoginCall::class.java)

    companion object {
        private const val username = "fake_address@gmail.com"
        private const val password = "hrfuqai@8979457"

        const val PROD_ENDPOINT = "https://any_api.com"
    }
}

interface ApiInterface {

    @GET("/v2/5def7b172f000063008e0aa2")
    fun getGrocery(): Call<GroceryList>

}