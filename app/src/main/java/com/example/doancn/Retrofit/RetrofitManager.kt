package com.example.doancn.Retrofit

import com.example.doancn.API.ISignUpApi
import com.example.doancn.API.Iapiexample
import com.example.doancn.API.IauthApi
import com.example.doancn.API.IvalidateTokenApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(Urls.url1).addConverterFactory(GsonConverterFactory.create()).build() // later
    }
    val example : Iapiexample by lazy {
        retrofit.create(Iapiexample::class.java)
    }
    val authapi : IauthApi by lazy {
        retrofit.create(IauthApi::class.java)
    }
    val validateTokenApi : IvalidateTokenApi by lazy {
        retrofit.create(IvalidateTokenApi::class.java)
    }
    val signupapi : ISignUpApi by lazy {
        retrofit.create(ISignUpApi::class.java)
    }
}
class Urls {
    companion object {
        const val url1 ="http://10.0.2.2:8081/"
        const val url2="http://10.0.2.2:6969/"
        const val url3=""
    }
}

