package com.example.doancn.API

import com.example.doancn.Models.SectionX
import retrofit2.http.GET
import retrofit2.http.Path

interface ISectionApi {

    @GET("/shift/{shiftId}/sections")
    suspend fun getSectionsOfShift(@Path("shiftId") shiftId: Long) : List<SectionX>
}