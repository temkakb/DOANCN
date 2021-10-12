package com.example.doancn.API

import com.example.doancn.Models.Account
import com.example.doancn.Models.AccountSignUp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IauthApi {
    @POST("/auth/login")
    suspend fun login(
        @Body account: Account
    ) : Map<String ,String>

    @POST("/auth/signup")
    suspend fun signup(@Body account: AccountSignUp)

    @POST("/auth/validatetoken")
    suspend fun validate(
        @Body map: Map<String,String>
    )

}