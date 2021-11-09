package com.example.doancn.Repository

import com.example.doancn.API.IEnrollmentApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import javax.inject.Inject

class EnrollmentRepository
@Inject constructor(
    private val enrollmentapi: IEnrollmentApi
) {
    suspend fun getclassenrollment(
        city: String,
        subjectId: Long?,
        token: String
    ): DataState<List<Classroom>?> {
        val response = enrollmentapi.getclassenrollment(city, subjectId, token)
        if (response.isSuccessful)
            return DataState.Success(response.body())
        else
            return DataState.Error("gigido")

    }

    suspend fun doEnroll(id: Long, token: String): DataState<String> {
        val response = enrollmentapi.doEnroll(id, token)
        if (response.isSuccessful)
            return DataState.Success(response.body()!!.string().toString())
        else
            return DataState.Error("gigido")
    }

    suspend fun doDeleteEnrollment(id: Long, token: String): DataState<String> {
        val response = enrollmentapi.doDeleteEnrollment(id, token)
        if (response.isSuccessful)
            return DataState.Success(response.body()!!.string().toString())
        else
            return DataState.Error("gigido")
    }
}