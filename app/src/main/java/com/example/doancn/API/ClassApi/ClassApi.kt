package com.example.doancn.API.ClassApi

import com.example.doancn.Models.Classroom
import com.example.doancn.Models.classModel.ClassQuest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface ClassApi {
    @POST("/classes")
    suspend fun createClass(
        @Body classroom: ClassQuest,
        @Header("Authorization") authorization: String
    ): Response<ResponseBody>

    @GET("/classes/mine")
    suspend fun getMyClass(@Header("Authorization") authorization: String): Response<List<Classroom>>

}