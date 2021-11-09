package com.example.doancn.Repository

import com.example.doancn.API.IauthApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Account
import com.example.doancn.Models.AccountSignUp
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authapi: IauthApi
) {
    suspend fun login(account: Account): DataState<Map<String, String>?> {


        val response = authapi.login(account)
        val result = response.body()
        if (response.isSuccessful)
            return DataState.Success(result)
        else return DataState.Error(response.errorBody()!!.string().toString())


    }

    suspend fun validate(map: Map<String, String>): DataState<String> {
        val response = authapi.validate(map)
        if (response.isSuccessful)
            return DataState.Success("Chào mừng bạn trở lại")
        else return DataState.Error("Phiên đăng nhập đã hết hạn")
    }

    suspend fun signup(account: AccountSignUp): DataState<String> {
        val response = authapi.signup(account)
        if (response.isSuccessful)
            return DataState.Success("Đăng ký thành công")
        else
            return DataState.Error(response.errorBody()!!.string().toString())
    }
}