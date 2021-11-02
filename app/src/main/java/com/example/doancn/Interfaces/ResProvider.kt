package com.example.demotranghome.Interfaces

import android.graphics.Typeface

interface ResProvider {
    fun getSelectedDayBackground(): Int
    fun getSelectedDayTextColor(): Int
    fun getDayTextColor(): Int
    fun getWeekDayTextColor(): Int
    fun getDayBackground(): Int
    fun getCustomFont(): Typeface?
}