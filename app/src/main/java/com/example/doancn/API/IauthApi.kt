package com.example.doancn.API

import com.example.doancn.Models.Account
import com.example.doancn.Models.AccountSignUp
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IauthApi {
    @POST("/auth/login")
    suspend fun login(
        @Body account: Account
    ): Response<Map<String, String>>

    @POST("/auth/signup")
    suspend fun signup(@Body account: AccountSignUp): Response<ResponseBody>

    @POST("/auth/validatetoken")
    suspend fun validate(
        @Body map: Map<String, String>
    ): Response<ResponseBody>

    @GET("/auth/forgotpassword")
    suspend fun forGotPassword(
        @Query("email") email: String,
        @Query("code") code: String?
    ): Response<ResponseBody>
}