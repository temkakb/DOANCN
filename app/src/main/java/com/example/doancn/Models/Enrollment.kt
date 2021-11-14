package com.example.doancn.Models

data class Enrollment (
    val accepted: Boolean,
    val classroom: Classroom,
    val dateCreated: String,
    val enrollmentId: Int,
    var nextPaymentAt: String,
    val startDate: String
)