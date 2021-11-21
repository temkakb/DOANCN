package com.example.doancn.Repository

import com.example.doancn.API.ClassApi.ClassApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
import com.example.doancn.Models.classModel.ClassQuest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ClassRepository @Inject constructor(
    private val classApi: ClassApi
) {
    suspend fun createClassroom(classroom: ClassQuest, token: String): Flow<DataState<String>> {
        return flow {
            try {
                var dataState: DataState<String> = DataState.Loading
                emit(dataState)
                val response = classApi.createClass(classroom = classroom, authorization = token)
                if (response.isSuccessful)
                    dataState = DataState.Success("Thêm thành công")
                else if (response.code() == 400) {
                    val error = response.errorBody()?.string().toString()
                    dataState = DataState.Error(error)
                }
                delay(1000)
                this@flow.emit(dataState)
            } catch (e: Exception) {
                DataState.Error(e.message.toString())
            }
        }
    }

    suspend fun getListClass(token: String): DataState<List<Classroom>> {
        return try {
            val response = classApi.getMyClass(token)
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

    suspend fun getUserOfClass(token: String, id: Long): DataState<List<UserMe>> {
        return try {
            val response = classApi.getUserOfClass(token,id)
            val result = response.body()
        return if (response.isSuccessful && result != null) {
            DataState.Success(result)
        } else {
            DataState.Error(response.errorBody().toString())
        }
        } catch (e: java.lang.Exception) {
            DataState.Error(e.message.toString())
        }
    }

    suspend fun updateStudentPayment(token: String, id: Int): Flow<DataState<String>>{
        return flow {
            try {
                var dataState: DataState<String> = DataState.Loading
                emit(dataState)
                val response = classApi.updateUserPayment(token,id)
                if (response.isSuccessful)
                    dataState = DataState.Success("Đóng tiền thành công")
                else if (response.code() == 400) {
                    val error = response.errorBody()?.string().toString()
                    dataState = DataState.Error(error)
                }
                delay(1000)
                this@flow.emit(dataState)
            } catch (e: Exception) {
                DataState.Error(e.message.toString())
            }
        }
    }
}