package com.example.doancn.Repository

import com.example.doancn.DI.DataState
import com.example.doancn.Models.QrCodeX
import com.example.doancn.Models.UserMe
import com.example.doancn.Retrofit.RetrofitManager

class AttendanceRepository {
    suspend fun getQrcodeToken( classId : Long,token: String) : QrCodeX {
        return RetrofitManager.attendanceapi.getQrcodeToken(classId,token)
    }
    suspend fun doAttendance(classId: Long,qrId:String,token:String)
    {
        RetrofitManager.attendanceapi.doAttendance(classId,qrId,token)
    }
    suspend fun getAttendancedStudents(sectionId: Long, token:String):DataState<List<UserMe>>{
        val response = RetrofitManager.attendanceapi.getAttendancedStudentsOfSection(token,sectionId)
        val result = response.body()
        return try {
            if (response.isSuccessful && result != null) {
                DataState.Success(result)
            } else {
                DataState.Error(response.errorBody().toString())
            }
        } catch (e: java.lang.Exception) {
            DataState.Error(e.message.toString())
        }
    }
}