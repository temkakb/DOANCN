package com.example.doancn.API.ProfileApi

import com.example.doancn.Models.UserMe
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface IUserApi {

    @GET("/user/getme")
    suspend fun  getme (@Header("Authorization") authorization : String) : Response<UserMe>

    @PUT("/user/update")
    suspend fun updateUser(@Header("Authorization") authorization : String,@Body user: UserMe):Response<ResponseBody>

    @PUT("/user/pass")
    suspend fun updatePassUser(@Header("Authorization") authorization : String,@Body map: Map<String,String>):Response<ResponseBody>

    @PUT("/user/img")
    suspend fun updateImgUser(@Header("Authorization") authorization : String, @Body map: Map<String,String>):Response<ResponseBody>

}
