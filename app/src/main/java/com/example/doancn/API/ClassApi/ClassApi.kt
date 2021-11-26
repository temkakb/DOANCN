package com.example.doancn.API.ClassApi

import com.example.doancn.Models.Classroom
import com.example.doancn.Models.User
import com.example.doancn.Models.classModel.ClassQuest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ClassApi {
    @POST("/classes")
    suspend fun createClass(
        @Body classroom: ClassQuest,
        @Header("Authorization") authorization: String
    ): Response<ResponseBody>

    @GET("/classes/mine")
    suspend fun getMyClass(@Header("Authorization") authorization: String): Response<List<Classroom>>

    @GET("/classes/{id}/students")
    suspend fun getListStudent(
        @Header("Authorization") authorization: String,
        @Path("id") classId: Long
    ): Response<List<User>>

    @Headers("Content-Type: application/json")
    @DELETE("/classes/{id}")
    suspend fun deleteClass(
        @Header("Authorization") authorization: String,
        @Path("id") classId: Long
    ): Response<ResponseBody>


    @PUT("/classes/{id}")
    suspend fun updateClass(
        @Header("Authorization") authorization: String,
        @Path("id") classId: Long,
        @Body classroom: ClassQuest
    ): Response<Classroom>

}