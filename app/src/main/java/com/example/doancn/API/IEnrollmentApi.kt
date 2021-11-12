package com.example.doancn.API

import com.example.doancn.Models.Classroom
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface IEnrollmentApi {

    @GET("/enrollments")
    suspend fun getclassenrollment(
        @Query("city") city: String,
        @Query("subjectId") subject: Long?,
        @Header("Authorization") token: String
    ): Response<List<Classroom>>

    @GET("/enrollments/classrooms/{id}")
    suspend fun doEnroll(@Path("id") id: Long, @Header("Authorization") token: String)
            : Response<ResponseBody>

    @DELETE("/enrollments/classrooms/{id}")
    suspend fun doDeleteEnrollment(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("/enrollments/search")
    suspend fun doSearch(
        @Query("keyword") keyword: String,
        @Header("Authorization") token: String
    ): Response<List<Classroom>>

}