package com.example.doancn.Models

data class Shift(
    val dateCreated: String,
    val dayOfWeek: DayOfWeek,
    val duration: Long,
    val shiftId: Int,
    val startAt: Long
)