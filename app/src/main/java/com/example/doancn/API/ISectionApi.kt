package com.example.doancn.API

import com.example.doancn.Models.SectionX
import com.example.doancn.Models.UserMe
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ISectionApi {

    @GET("/shift/{shiftId}/sections")
    suspend fun getSectionsOfShift(@Path("shiftId") shiftId: Long) : List<SectionX>

    @GET("/shift/section/{Id}/students")
    suspend fun getAttendanceStudents(@Header("Authorization") authorization: String, @Path("Id") sectionId: Long) : List<UserMe>
}