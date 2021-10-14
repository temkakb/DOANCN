package com.example.doancn.API

import com.example.doancn.Models.Classroom
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface IEnrollmentApi {

    @GET("/enrollments")
    suspend fun getclassenrollment(@Query("city") city: String,@Header("Authorization") token: String) : List<Classroom>
    @GET("/enrollments/classrooms/{Id}")
    suspend fun doEnroll (@Path("Id") Id: Long,@Header("Authorization") token: String)
}