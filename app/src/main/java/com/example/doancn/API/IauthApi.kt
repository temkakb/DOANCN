package com.example.doancn.API

import com.example.doancn.Models.Account
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IauthApi {
    @POST("/auth/login")
    suspend fun login(
        @Body account: Account
    ) : Map<String ,String>

}