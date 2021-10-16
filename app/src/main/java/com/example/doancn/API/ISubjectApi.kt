package com.example.doancn.API

import retrofit2.http.GET


interface ISubjectApi {
    @GET("/subjects/enrrollment")
    suspend fun getSubjects () : List<com.example.doancn.Models.Subject>
}