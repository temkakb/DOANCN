package com.example.doancn.Repository

import com.example.doancn.Models.Subject
import com.example.doancn.Retrofit.RetrofitManager


class SubjectRepository {
    suspend fun getSubjects () : List<Subject>{
        return RetrofitManager.subjectapi.getSubjects()
    }
}