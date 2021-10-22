package com.example.doancn.API.ProfileApi

import com.example.doancn.Models.Parent
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IParentApi {

    @GET("/user/{id}/parent")
    fun  getUserParent (@Header("Authorization") authorization : String, @Path("id") id : Int) : Call<List<Parent>>

    @POST("/user/{id}/Parent")
    fun addParent(@Header("Authorization") authorization : String, @Path("id") id : Int, @Body map: Map<String,String>): Call<Unit>

    @PUT("/user/Parent/{id}")
    fun updateParent(@Header("Authorization") authorization : String, @Path("id") id : Int
                     , @Body map: Map<String,String>): Call<Unit>

    @DELETE("/user/{id}/Parent/{parentid}")
    fun deleteParent(@Header("Authorization") authorization : String, @Path("id") id : Int
                     , @Path("parentid") parentid : Int): Call<Unit>

}