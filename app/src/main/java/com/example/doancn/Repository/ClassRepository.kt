package com.example.doancn.Repository

import android.util.Log
import com.example.doancn.API.ClassApi.ClassApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.User
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

    suspend fun getListStudent(token: String, classId: Long): DataState<List<User>> {
        return try {
            val response = classApi.getListStudent(token, classId)
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

    suspend fun deleteClass(classId: Long, token: String): DataState<String> {
        return try {

            val response = classApi.deleteClass(token, classId)
            if (response.isSuccessful) {
                DataState.Success(response.body().toString())
            } else {
                DataState.Error(response.errorBody().toString())

            }
        } catch (e: java.lang.Exception) {
            Log.d("deleteClass E", e.message.toString())
            DataState.Error(e.message.toString())
        }
    }

    suspend fun updateClass(
        classId: Long,
        classRoom: ClassQuest,
        token: String
    ): DataState<Classroom> {
        return try {
            val response = classApi.updateClass(token, classId = classId, classroom = classRoom)
            if (response.isSuccessful) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(response.errorBody().toString())
            }
        } catch (e: java.lang.Exception) {
            DataState.Error(e.message.toString())
        }
    }
}
