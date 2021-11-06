package com.example.demotranghome.Models

import java.io.Serializable
import java.util.*

class CalendarDay(val year: Int, val month: Int, day: Int) : Serializable {
    var state: Int = DEFAULT
    val day: GregorianCalendar

    override fun toString(): String {
        return "Day: " + day[Calendar.DAY_OF_MONTH] + " State: " + state
    }

    companion object {
        const val DEFAULT = 0
        const val SELECTED = 1
        const val TODAY = 2
    }

    init {
        this.day = GregorianCalendar(year, month, day)
    }
}