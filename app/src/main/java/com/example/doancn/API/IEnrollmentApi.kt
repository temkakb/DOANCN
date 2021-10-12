package com.example.doancn.API

import com.example.doancn.Models.Classroom
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface IEnrollmentApi {

    @GET("/enrollments")
    fun getclassenrollment(@Query("city") city: String,@Header("Authorization") token: String) : List<Classroom>
}