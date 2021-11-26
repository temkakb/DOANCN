package com.example.doancn.Repository

import com.example.doancn.Models.SectionX
import com.example.doancn.Models.UserMe
import com.example.doancn.Retrofit.RetrofitManager

class SectionsRepository {
    suspend fun getSectionsOfShift(shiftId : Long ) : List<SectionX>{
        return RetrofitManager.sectionapi.getSectionsOfShift(shiftId)
    }

    suspend fun getAttendanceStudents(sectionId: Long, token : String): List<UserMe>{
        return RetrofitManager.sectionapi.getAttendanceStudents(token,sectionId)
    }
}