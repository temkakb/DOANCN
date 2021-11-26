package com.example.doancn.Repository

import com.example.doancn.API.ProfileApi.IUserApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.UserMe
import com.example.doancn.ViewModels.UserViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepository@Inject constructor(
    private val userapi: IUserApi
) {

    suspend fun updateimg(token: String, map: Map<String, String>):DataState<String>{
        val response = userapi.updateImgUser(token,map)
        if (response.isSuccessful)
            return DataState.Success("Đổi hình ảnh thành công")
        else
            return DataState.Error(response.errorBody()!!.string().toString())
    }

    suspend fun updatePassUser(token: String, map: Map<String, String>):DataState<String>{
        val response = userapi.updatePassUser(token,map)
        val result = response.body()
        if (response.isSuccessful)
            return DataState.Success(result!!.string())
        else
            return DataState.Error(response.errorBody()!!.string().toString())
    }

    suspend fun updateuser(token: String, user: UserMe):DataState<String>{
        val response = userapi.updateUser(token,user)
        if (response.isSuccessful)
            return DataState.Success("Thay đổi thành công")
        else
            return DataState.Error(response.errorBody()!!.string().toString())
    }


    suspend fun getme(token: String) : DataState<UserMe>{
        val response = userapi.getme(token)
        val result = response.body()
        return try {
            if (response.isSuccessful && result != null) {
                DataState.Success(result)
            } else {
                DataState.Error(response.errorBody().toString())
            }
        } catch (e: java.lang.Exception) {
            DataState.Error(e.message.toString())
        }
    }


}