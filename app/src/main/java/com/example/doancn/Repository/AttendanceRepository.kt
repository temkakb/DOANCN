package com.example.doancn.Repository

import com.example.doancn.Models.QrCodeX
import com.example.doancn.Retrofit.RetrofitManager

class AttendanceRepository {
    suspend fun getQrcodeToken( classId : Long,token: String) : QrCodeX {
        return RetrofitManager.attendanceapi.getQrcodeToken(classId,token)
    }
}