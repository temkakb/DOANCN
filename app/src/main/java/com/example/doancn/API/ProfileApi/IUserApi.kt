package com.example.doancn.API.ProfileApi

import com.example.doancn.Models.UserMe
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface IUserApi {

    @GET("/user/getme")
    fun  getme (@Header("Authorization") authorization : String) : Call<UserMe>

    @GET("/user/class/{classid}")
    suspend fun getUserOfClass(@Header("Authorization") authorization: String, @Path("classid") id: Long) : Response<List<UserMe>>

    @PUT("/user/{id}")
    fun updateUser(@Header("Authorization") authorization : String,@Path("id") id : Int,@Body user: UserMe):Call<Unit>

    @PUT("/user/pass/{id}")
    fun updatePassUser(@Header("Authorization") authorization : String,@Path("id") id : Int,@Body map: Map<String,String>):Call<ResponseBody>

    @PUT("/user/img/{id}")
    fun updateImgUser(@Header("Authorization") authorization : String,@Path("id") id : Int, @Body map: Map<String,String>):Call<Unit>

    @PUT("/user/class/pay/{id}")
    fun updateUserPayment(@Header("Authorization") authorization : String, @Path("id") id : Int): Response<Unit>
}
