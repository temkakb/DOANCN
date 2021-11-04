package com.example.doancn.Repository

import com.example.doancn.API.ClassApi.SubjectApi
import javax.inject.Inject

class SubjectsRepository @Inject constructor(
    private val subjectApi: SubjectApi
) {
//    suspend fun getSubject(
//        token: String
//    ): Flow<DataState<List<ClassQuest.Subject>>> {
//        return flow {
//            this.emit(DataState.Loading)
//            delay(1000)
//            try {
//                val list = subjectApi.get(token)
//                Log.d("TAG", list.toString())
//                this.emit(DataState.Success(list))
//            } catch (e: Exception) {
//                this.emit(DataState.Error(e.message))
//                Log.d("TAG", e.toString())
//
//            }
//        }
    // }

}