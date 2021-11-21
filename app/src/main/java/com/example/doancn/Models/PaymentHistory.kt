package com.example.doancn.Models

data class PaymentHistory(
    val amount: Double,
    val dateCreated: String,
    val enrollment: Enrollment,
    val paymentHistoryId: Int
)