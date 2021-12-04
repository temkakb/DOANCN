package com.example.doancn.Repository


import com.example.doancn.API.ClassApi.ClassApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.*
import com.example.doancn.Models.classModel.ClassQuest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.EOFException
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


    suspend fun deleteClass(classId: Long, token: String): DataState<String> {
        return try {

            val response = classApi.deleteClass(token, classId)
            if (response.isSuccessful) {
                DataState.Success(response.body().toString())
            } else {
                DataState.Error(response.errorBody().toString())

            }
        } catch (e: java.lang.Exception) {
            DataState.Error(e.message.toString())
        }
    }

    suspend fun getHomeWorks(token: String, id: Long): DataState<List<HomeWorkX>?> {
        val response = classApi.getHomeWork(id, token)
        return if (response.isSuccessful) {
            DataState.Success(response.body())
        } else {
            DataState.Error(response.errorBody().toString())
        }
    }

    suspend fun getSubmissions(
        id: Long,
        homeworkId: Long,
        token: String
    ): DataState<List<SubmissionX>?> {
        val response = classApi.getSubmissions(id, homeworkId, token)
        return if (response.isSuccessful) {
            DataState.Success(response.body())
        } else {
            DataState.Error(response.errorBody().toString())
        }

    }

    suspend fun getSubmission(
        id: Long,
        homeworkId: Long,
        token: String
    ): DataState<SubmissionX?> {
        return try {
            val response = classApi.getSubmission(id, homeworkId, token)
            if (response.isSuccessful)

                DataState.Success(response.body())
            else
                DataState.Error(response.errorBody().toString())

        } catch (e: EOFException) {
            DataState.Success(null)
        }

    }

    suspend fun deleteSubmission(
        id: Long,
        submissionId: Long,
        token: String
    ): DataState<String> {
        val response = classApi.deleteSubmission(id, submissionId, token)
        return if (response.isSuccessful)
            DataState.Success("Hủy gửi thành công")
        else
            DataState.Error(response.errorBody().toString())
    }

    suspend fun deleteHomeWork(
        id: Long,
        homeWorkId: Long,
        token: String
    ): DataState<String> {
        val response = classApi.deleteHomeWork(id, homeWorkId, token)
        return if (response.isSuccessful)
            DataState.Success("Xóa thành công")
        else
            DataState.Error(response.errorBody().toString())
    }


    suspend fun getUserOfClass(token: String, id: Long): DataState<List<UserMe>> {
        return try {
            val response = classApi.getUserOfClass(token, id)
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


    suspend fun updateClass(
        classId: Long,
        classRoom: ClassQuest,
        token: String
    ): DataState<Classroom> {
        return try {
            val response =
                classApi.updateClass(token, classId = classId, classroom = classRoom)
            if (response.isSuccessful) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(response.errorBody()!!.string().toString())
            }
        } catch (e: java.lang.Exception) {
            DataState.Error(e.message.toString())
        }
    }

    suspend fun updateStudentPayment(token: String, id: Int): Flow<DataState<String>> {
        return flow {
            try {
                var dataState: DataState<String> = DataState.Loading
                emit(dataState)
                val response = classApi.updateUserPayment(token, id)
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


