package com.example.doancn.API.ProfileApi

import com.example.doancn.Models.Parent
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface IParentApi {

    @GET("/user/parent")
    suspend fun  getUserParent (@Header("Authorization") authorization : String) : Response<List<Parent>>

    @POST("/user/Parent/add")
    suspend fun addParent(@Header("Authorization") authorization : String, @Body map: Map<String,String>): Response<Unit>

    @PUT("/user/Parent/{id}")
    suspend fun updateParent(@Header("Authorization") authorization : String, @Path("id") id : Int
                     , @Body map: Map<String,String>): Response<Unit>

    @DELETE("/user/Parent/{id}")
    suspend fun deleteParent(@Header("Authorization") authorization : String
                     , @Path("id") id : Int): Response<Unit>

}