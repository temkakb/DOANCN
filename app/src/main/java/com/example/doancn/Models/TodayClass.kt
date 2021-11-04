package com.example.doancn.Models

class TodayClass() {

    var classrooms: ArrayList<Classroom> = ArrayList()

    fun ClassroomsForDate(date: String?): ArrayList<Classroom> {
        val todayClass : ArrayList<Classroom> = ArrayList()
        for (classroom in classrooms) {
            for(shift in classroom.shifts)
            {
                if (shift.dayOfWeek.dowName == date) todayClass.add(classroom)
            }
        }
        return todayClass
    }

    // Chưa biết sort sao cho hợp lý Sort này lỗi bà rồi :(((
    fun sortTodayClass(todayClass : ArrayList<Classroom>, date: String) : ArrayList<Classroom>{
        if(todayClass.count() > 1)
        {
            for (classroom1 in todayClass) {
                for(shift1 in classroom1.shifts)
                {
                    if (shift1.dayOfWeek.dowName != date)  classroom1.shifts.remove(shift1)
                }
            }
            todayClass.sortBy { classroom -> classroom.shifts[0].startAt }
        }
        return todayClass
    }
}