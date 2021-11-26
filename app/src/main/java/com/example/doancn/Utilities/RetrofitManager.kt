package com.example.doancn.Retrofit

import com.example.doancn.API.*
import com.example.doancn.API.ProfileApi.IParentApi
import com.example.doancn.API.ProfileApi.IUserApi
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
    val userapi : IUserApi by lazy {
        retrofit.create(IUserApi::class.java)
    }
    val  parentapi : IParentApi by lazy {
        retrofit.create(IParentApi::class.java)
    }
    val sectionapi: ISectionApi by lazy {
        retrofit.create(ISectionApi::class.java)
    }
    val attendanceapi : IAttendanceApi by lazy {
        retrofit.create(IAttendanceApi::class.java)
    }
}
class Urls {
    companion object {
        const val url1 ="http://10.0.2.2:8081/"
        const val url2="10.0.2.2"
        const val port=6969
    }
}

