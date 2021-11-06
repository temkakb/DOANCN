package com.example.doancn.API

import com.example.doancn.Models.QrCodeX
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface IAttendanceApi {
    @GET("/classes/{id}/qrcode")
    suspend fun getQrcodeToken(@Path("id") classId : Long,@Header("Authorization") token: String) : QrCodeX
}