package com.example.doancn.API.ProfileApi

import com.example.doancn.Models.Account
import com.example.doancn.Models.UserMe
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface IUpdateUser {
    @PUT("/user/update")
    fun updateUser(@Body user: UserMe)

    @PUT("/user/img/{id}")
    fun updateImgUser(@Header("Authorization") authorization : String,@Path("id") id : Int, @Body map: Map<String,String>):Call<Unit>
}