package com.example.demotranghome.Interfaces

import com.example.demotranghome.Adapters.CalendarWeekAdapter

interface DateWatcher {
    fun getStateForDate(year: Int, month: Int, day: Int, view: CalendarWeekAdapter.DayViewHolder?): Int
}