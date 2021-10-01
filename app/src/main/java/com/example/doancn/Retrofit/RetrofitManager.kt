package com.example.doancn.Retrofit

import com.example.doancn.API.APIexample
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(Urls.url1).addConverterFactory(GsonConverterFactory.create()).build() // later
    }
    val example : APIexample by lazy {
        retrofit.create(APIexample::class.java)
    }
}

class Urls {
    companion object {
        const val url1 ="http://localhost:8081/"
        const val url2=""
        const val url3=""
    }

}

