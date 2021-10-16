package com.example.doancn.API

import com.example.doancn.Models.UserMe
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface IGetmeApi {
    @GET("/user/getme")
    fun  getme (@Header("Authorization") authorization : String) : Call<UserMe>
}