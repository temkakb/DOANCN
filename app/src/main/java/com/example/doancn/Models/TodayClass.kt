package com.example.doancn.Models

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class TodayClass() {

    var classrooms: ArrayList<Classroom> = ArrayList()

    fun ClassroomsForDate(date: String?, selected: String): ArrayList<Classroom> {
        val todayClass : ArrayList<Classroom> = ArrayList()
        for (classroom in classrooms) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val startDay = LocalDate.parse(classroom.startDate, formatter)
            val selectDay = LocalDate.parse(selected,formatter)
            for(shift in classroom.shifts)
            {
                if (shift.dayOfWeek.dowName == date
                    && (startDay.isBefore(selectDay)
                            || startDay.isEqual(selectDay)))

                                todayClass.add(classroom)
            }
            if(todayClass.count() > 1)
            todayClass.sortBy { i -> i.shifts[0].startAt }
        }
        return todayClass
    }

    fun TeacherClassroomsForDate(date: String?,selected: String): ArrayList<Classroom> {
        val todayClass : ArrayList<Classroom> = ArrayList()
        for (classroom in classrooms) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val startDay = LocalDate.parse(classroom.startDate, formatter)
            val selectDay = LocalDate.parse(selected,formatter)
            for(shift in classroom.shifts)
            {
                if (shift.dayOfWeek.dowName == date
                    && (startDay.isBefore(selectDay)
                            || startDay.isEqual(selectDay)))
                    todayClass.add(classroom)
            }
            if(todayClass.count() > 1)
            todayClass.sortBy { i -> i.shifts[0].startAt }
        }
        return todayClass
    }
}