package com.example.doancn.Models

data class Enrollment (
    val accepted: Boolean,
    val classroom: Classroom,
    val dateCreated: String,
    val enrollmentId: Int,
    val nextPaymentAt: String,
    val startDate: String
)