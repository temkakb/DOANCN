package com.example.doancn.API.ClassApi

import com.example.doancn.Models.Classroom
import com.example.doancn.Models.HomeWorkX
import com.example.doancn.Models.SubmissionX
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


    @GET("/classes/{id}/homework")
    suspend fun getHomeWork (@Path("id") id : Long, @Header("Authorization") token : String) : Response<List<HomeWorkX>>
    @GET("/classes/{id}/homework/{homeworkId}")
    suspend fun getSubmissions(@Path("id") id : Long, @Path("homeworkId") homeworkId :Long, @Header("Authorization") token : String)
    : Response<List<SubmissionX>>

}