package com.example.doancn.Models

import java.time.LocalDate

data class Classroom (
    val classId: Long,
    val name: String,
    val shortDescription: String,
    val about: String,
    val fee: Double,
    val currentAttendanceNumber: Int,
    val startDate: String,
    val dateCreated: String,
    val teacher: User,
    val subject: Subject,
    val location : Location,
    val option: PaymentOption,
    val enrolled: Boolean
)