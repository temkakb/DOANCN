package com.example.doancn.API.ClassApi

import com.example.doancn.Models.classModel.ClassQuest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface ClassApi {
    @POST("/classes")
    fun createClass(
        @Body classroom: ClassQuest,
        @Header("Authorization") authorization: String
    ): Call<ResponseBody>
}