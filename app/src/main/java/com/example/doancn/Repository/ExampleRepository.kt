package com.example.doancn.Repository


import com.example.doancn.Retrofit.RetrofitManager

class ExampleRepository {
    suspend fun example () : String{
        return RetrofitManager.example.example()
    }

}