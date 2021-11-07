package com.example.doancn.Models

import java.io.Serializable

data class Shift(
    val dateCreated: String,
    val dayOfWeek: DayOfWeek,
    val duration: Long,
    val shiftId: Int,
    val startAt: Long
) : Serializable