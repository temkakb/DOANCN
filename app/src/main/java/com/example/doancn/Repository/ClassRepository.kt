package com.example.doancn.Repository

import com.example.doancn.API.ClassApi.ClassApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.HomeWorkX
import com.example.doancn.Models.SubmissionX
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
    suspend fun  getHomeWorks (token : String, id : Long) : DataState<List<HomeWorkX>?>{
        val response = classApi.getHomeWork(id,token)
        if (response.isSuccessful){
             return DataState.Success(response.body())
        }
        else{
         return   DataState.Error(response.errorBody().toString())
        }
    }
    suspend fun getSubmissions (id: Long, homeworkId :Long,token : String) : DataState<List<SubmissionX>?>{
        val response = classApi.getSubmissions(id,homeworkId,token )
        if (response.isSuccessful){
            return DataState.Success(response.body())
        }
        else{
            return   DataState.Error(response.errorBody().toString())
        }

    }
}