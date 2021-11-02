package com.example.doancn.Models

class TodayClass() {

    var classrooms: ArrayList<Classroom> = ArrayList()
    var enrollments : ArrayList<Enrollment> = ArrayList()

    fun ClassroomsForDate(date: String?): ArrayList<Classroom> {
        val todayClass : ArrayList<Classroom> = ArrayList()
        for (classroom in classrooms) {
            for(shift in classroom.shifts)
            {
                if (shift.dayOfWeek.dowName == date) todayClass.add(classroom)
            }
        }
        if(todayClass.count() > 1)
        {
            for (classroom in todayClass) {
                for(shift in classroom.shifts)
                {
                    if (shift.dayOfWeek.dowName != date)  classroom.shifts.remove(shift)
                }
            }
            todayClass.sortBy { classroom -> classroom.shifts[0].startAt }
        }
        return todayClass
    }
}