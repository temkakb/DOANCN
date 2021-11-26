package com.example.doancn.Models

data class SubmissionX(
    val dateCreated: String,
    val fileId: Long,
    val late: Boolean,
    val name: String,
    val student : User,
    val sizeInByte: Int
)