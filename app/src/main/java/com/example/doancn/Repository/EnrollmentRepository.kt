package com.example.doancn.Repository

import com.example.doancn.Models.Classroom
import com.example.doancn.Retrofit.RetrofitManager

class EnrollmentRepository {
    suspend fun getclassenrollment (city: String, token: String): List<Classroom>{
        return  RetrofitManager.enrollmentapi.getclassenrollment(city,token);
    }
}