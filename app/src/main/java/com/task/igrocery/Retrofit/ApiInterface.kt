package com.task.igrocery.Retrofit

import com.task.igrocery.Model.GroceryList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/v2/5def7b172f000063008e0aa2")
    fun getGrocery(): Call<GroceryList>

}