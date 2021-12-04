package com.example.doancn.Repository

import com.example.doancn.API.IEnrollmentApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
import java.lang.Exception
import javax.inject.Inject

class EnrollmentRepository
@Inject constructor(
    private val enrollmentapi: IEnrollmentApi
) {
//    companion object {
//        const val ENROLL_SUCCESS: Int = 0
//        const val CANCEL_ENROLL_SUCCESS: Int = 1
//    }

    suspend fun getclassenrollment(
        city: String,
        subjectId: Long?,
        token: String
    ): DataState<List<Classroom>?> {

        val response = enrollmentapi.getclassenrollment(city, subjectId, token)
        if (response.isSuccessful)

            return DataState.Success(response.body())
        else
            return DataState.Error(response.errorBody()!!.string().toString())

    }


    suspend fun doEnroll(id: Long, token: String): DataState<String> {
        val response = enrollmentapi.doEnroll(id, token)
        if (response.isSuccessful)
            return DataState.Success("Đăng ký thành công")
        else
            return DataState.Error(response.errorBody()!!.string().toString())
    }

    suspend fun doDeleteEnrollment(id: Long, token: String): DataState<String> {
        val response = enrollmentapi.doDeleteEnrollment(id, token)
        if (response.isSuccessful)
            return DataState.Success("Hủy đăng ký thành công")
        else
            return DataState.Error(response.errorBody()!!.string().toString())
    }

    suspend fun dosSearch(keyword: String, token: String): DataState<List<Classroom>?> {
        val response = enrollmentapi.doSearch(keyword, token)
        if (response.isSuccessful) {
            return DataState.Success(response.body())

        } else
            return DataState.Error(response.errorBody()!!.string().toString())
    }

    suspend fun getEnrollment(classId:Long,token: String): DataState<ArrayList<UserMe>> {
        return try {
            val response = enrollmentapi.getListEnrollment(classId,token)
            if(response.isSuccessful){
                DataState.Success(response.body()!!)
            }else
                 DataState.Error(response.errorBody()!!.string().toString())

        }catch (e:Exception){
            DataState.Error(e.message.toString())
        }
    }
    suspend fun acceptEnrollment(classId: Long, userId: Int, token: String): DataState<Boolean>{
        return try {
            val response = enrollmentapi.acceptEnrollment(classId,userId,token)
            if(response.isSuccessful){
                DataState.Success(true)
            }else
                DataState.Error(response.errorBody()!!.string().toString())

        }catch (e:Exception){
            DataState.Error(e.message.toString())
        }
    }
}