package com.example.doancn.Repository

import android.util.Log
import com.example.doancn.API.ClassApi.ClassApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.classModel.ClassQuest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class ClassRepository @Inject constructor(
    private val classApi: ClassApi
) {
    suspend fun createClassroom(classroom: ClassQuest, token: String): Flow<DataState<String>> {
        return flow {
            //this.emit(DataState.Loading)
            var dataState: DataState<String> = DataState.Loading
            try {
                val call = classApi.createClass(classroom = classroom, authorization = token)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.code() == 400) {
                            val error = response.errorBody()?.string().toString()

                            dataState = DataState.Error(error)
                            Log.d("Thêm that bai", response.toString())
                            Log.d("dataState", dataState.toString())

                        } else {
                            dataState = DataState.Success("Thêm thành công")
                            Log.d("Thêm thành công", response.toString())
                            Log.d("dataState", dataState.toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    }

                })
            } catch (e: HttpException) {
                Log.d("HttpException", e.message())
            }
            delay(1000)
            if (dataState is DataState.Error)
                Log.d("after delay", (dataState as DataState.Error).data)
            this@flow.emit(dataState)
        }
    }
}