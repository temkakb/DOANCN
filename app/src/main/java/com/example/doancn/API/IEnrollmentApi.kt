package com.example.doancn.API

import com.example.doancn.Models.Classroom
import retrofit2.http.*

interface IEnrollmentApi {

    @GET("/enrollments")
    suspend fun getclassenrollment(@Query("city") city: String,@Query("subject") subject: String?,@Header("Authorization") token: String) : List<Classroom>

    @GET("/enrollments/classrooms/{id}")
    suspend fun doEnroll (@Path("id") id: Long,@Header("Authorization") token: String)

    @DELETE("/enrollments/classrooms/{id}")
    suspend fun doDeleteEnrollment(@Path("id") id: Long,@Header("Authorization") token: String)
}