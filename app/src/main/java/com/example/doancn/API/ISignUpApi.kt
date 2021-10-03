package com.example.doancn.API

import com.example.doancn.Models.AccountSignUp
import retrofit2.http.Body
import retrofit2.http.POST

interface ISignUpApi {
    @POST("/auth/signup")
    suspend fun signup(@Body account: AccountSignUp)
}