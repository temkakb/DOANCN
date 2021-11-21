package com.example.doancn.API.ClassApi

import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
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



    @GET("/classes/users/{classid}")
    suspend fun getUserOfClass(@Header("Authorization") authorization: String, @Path("classid") id: Long) : Response<List<UserMe>>
    @PUT("/classes/pay/{id}")
    suspend fun updateUserPayment(@Header("Authorization") authorization : String, @Path("id") id : Int) : Response<ResponseBody>
}