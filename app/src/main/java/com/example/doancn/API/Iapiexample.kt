package com.example.doancn.API

import retrofit2.http.GET

interface Iapiexample {
    @GET("/example1")
    suspend fun example () : String
}