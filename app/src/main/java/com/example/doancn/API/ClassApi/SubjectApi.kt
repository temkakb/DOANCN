package com.example.doancn.API.ClassApi;

import com.example.doancn.Models.classModel.ClassQuest
import retrofit2.http.GET
import retrofit2.http.Header

interface SubjectApi {
    @GET("/subjects")
    suspend fun get(@Header("Authorization") authorization: String): List<ClassQuest.Subject>
}
