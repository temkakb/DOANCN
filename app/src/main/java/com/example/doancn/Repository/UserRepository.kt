package com.example.doancn.Repository

import com.example.doancn.API.IauthApi
import com.example.doancn.API.ProfileApi.IUserApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
import retrofit2.Response
import javax.inject.Inject

class UserRepository@Inject constructor(
    private val userapi: IUserApi
) {
    suspend fun getUserOfClass(token: String, id: Long): DataState<List<UserMe>> {
        return try {
            val response = userapi.getUserOfClass(token,id)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                DataState.Success(result)
            } else {
                DataState.Error(response.errorBody().toString())
            }
        } catch (e: java.lang.Exception) {
            DataState.Error(e.message.toString())
        }
    }

    fun updateStudentPayment(token: String, id: Int): Response<Unit>{
        return userapi.updateUserPayment(token,id)
    }
}