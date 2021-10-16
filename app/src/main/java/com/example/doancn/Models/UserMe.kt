package com.example.doancn.Models

data class UserMe(
    val account: AccountX,
    val address: String,
    val attendances: List<Attendace>,
    val currentWorkPlace: String,
    val dateCreated: String,
    val dob: String,
    val educationLevel: String,
    val enrollments: List<Enrollment>,
    val gender: GenderX,
    val image: String,
    var name: String,
    val parents: List<Parent>,
    val phoneNumber: String,
    val userId: Int
)