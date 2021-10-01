package com.example.doancn.API

import retrofit2.http.Body
import retrofit2.http.POST

interface ValidateTokenApi {
    @POST("/auth/validatetoken")
    suspend fun validate(
        @Body map: Map<String,String>
    )
}