package com.example.doancn.API

import com.example.doancn.Models.QrCodeX
import com.example.doancn.Models.UserMe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface IAttendanceApi {
    @GET("classes/section/{id}/attendance/students")
    suspend fun getAttendancedStudentsOfSection(@Header("Authorization") authorization: String, @Path("id") id: Long) : Response<List<UserMe>>
    @GET("/classes/{id}/qrcode")
    suspend fun getQrcodeToken(@Path("id") classId : Long,@Header("Authorization") token: String) : QrCodeX
    @GET("/classes/{id}/attendance/{qrId}")
    suspend fun doAttendance(@Path("id") classId : Long, @Path("qrId") qrId : String, @Header("Authorization") token: String)
}