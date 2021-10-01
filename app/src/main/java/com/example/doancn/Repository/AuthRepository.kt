package com.example.doancn.Repository

import com.example.doancn.Models.Account
import com.example.doancn.Retrofit.RetrofitManager

class AuthRepository {
    suspend fun login (account : Account) : Map<String,String>  {
        return RetrofitManager.authapi.login(account)
    }
    suspend fun validate (map: Map<String,String>) {
        RetrofitManager.validateTokenApi.validate(map)
    }
}