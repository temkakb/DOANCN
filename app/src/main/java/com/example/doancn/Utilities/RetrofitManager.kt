package com.example.doancn.Retrofit

import com.example.doancn.API.IEnrollmentApi
import com.example.doancn.API.ISubjectApi
import com.example.doancn.API.Iapiexample
import com.example.doancn.API.IauthApi
import com.example.doancn.API.*
import com.example.doancn.API.ProfileApi.IUpdateUser
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
    val enrollmentapi : IEnrollmentApi by lazy {
        retrofit.create(IEnrollmentApi::class.java)
    }
    val subjectapi : ISubjectApi by lazy {
        retrofit.create(ISubjectApi::class.java)
    }
    val getmeapi : IGetmeApi by lazy {
        retrofit.create(IGetmeApi::class.java)
    }
    val updateuserapi : IUpdateUser by lazy {
        retrofit.create(IUpdateUser::class.java)
    }
}
class Urls {
    companion object {
        const val url1 ="http://10.0.2.2:8081/"
    }
}

