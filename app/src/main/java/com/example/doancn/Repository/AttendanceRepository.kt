package com.example.doancn.Repository

import com.example.doancn.API.IAttendanceApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.QrCodeX
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val iAttendanceApi: IAttendanceApi
){
    suspend fun getQrcodeToken( classId : Long,token: String) : DataState<QrCodeX?> {
        val response= iAttendanceApi.getQrcodeToken(classId,token)
        if (response.isSuccessful)
            return DataState.Success(response.body())
        else
        return  DataState.Error(response.errorBody()!!.string().toString())
    }
    suspend fun doAttendance(classId: Long,qrId:String,token:String) : DataState<String>
    {
        val response= iAttendanceApi.doAttendance(classId,qrId,token)
        if (response.isSuccessful)
            return DataState.Success("Điểm danh thành công")
        else
            return  DataState.Error(response.errorBody()!!.string().toString())
    }
}