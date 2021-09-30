package com.example.doancn.API

import retrofit2.http.GET

interface APIexample {
    @GET("/example1")
    suspend fun example () : String
}