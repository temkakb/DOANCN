package com.example.doancn.Repository

import com.example.doancn.Models.Classroom
import com.example.doancn.Retrofit.RetrofitManager

class EnrollmentRepository {
    suspend fun getclassenrollment (city: String, subject: String?,token: String): List<Classroom>{
        return  RetrofitManager.enrollmentapi.getclassenrollment(city,subject,token)
    }
    suspend fun doEnroll (id: Long, token: String) {
        RetrofitManager.enrollmentapi.doEnroll(id,token)
    }
    suspend fun doDeleteEnrollment(id: Long,token: String){
        RetrofitManager.enrollmentapi.doDeleteEnrollment(id,token)
    }
}