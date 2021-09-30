package com.example.doancn.Repository

import com.example.doancn.Retrofit.RetrofitIntance

class ExampleRepository {
    suspend fun example () : String{
        return RetrofitIntance.example.example()
    }

}