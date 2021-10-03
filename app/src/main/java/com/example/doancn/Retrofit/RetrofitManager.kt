package com.example.doancn.Retrofit

import com.example.doancn.API.APIexample
import com.example.doancn.API.AuthApi
import com.example.doancn.API.ValidateTokenApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(Urls.url2).addConverterFactory(GsonConverterFactory.create()).build() // later
    }
    val example : APIexample by lazy {
        retrofit.create(APIexample::class.java)
    }
    val authapi : AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
    val validateTokenApi : ValidateTokenApi by lazy {
        retrofit.create(ValidateTokenApi::class.java)
    }

}
class Urls {
    companion object {
        const val url1 ="http://10.0.2.2:8081/"
        const val url2="http://10.0.2.2:6969/"
        const val url3=""
    }

}

