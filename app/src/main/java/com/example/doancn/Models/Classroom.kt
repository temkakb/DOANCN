package com.example.doancn.Models

import java.io.Serializable


data class Classroom(
    val classId: Long,
    val name: String = "",
    val shortDescription: String = "",
    val about: String = "",
    val fee: Double = 0.0,
    val currentAttendanceNumber: Int,
    val startDate: String,
    val dateCreated: String,
    val teacher: User,
    val subject: Subject,
    val location: Location,
    val option: PaymentOption,
    var enrolled: Boolean,
    val shifts: ArrayList<Shift>,
    val announcements: ArrayList<Announcement> = ArrayList()
) : Serializable