package com.example.doancn.Retrofit

import com.example.doancn.API.APIexample
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitIntance {
    private val retrofit by lazy {
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).build() // later
    }
    val example : APIexample by lazy {
        retrofit.create(APIexample::class.java)
    }
}
