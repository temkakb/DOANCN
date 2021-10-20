package com.example.doancn.Models


data class UserMe(
    val account: AccountX,
    var address: String,
    val attendances: List<Attendace>,
    var currentWorkPlace: String,
    val dateCreated: String,
    var dob: String,
    var educationLevel: String,
    val enrollments: List<Enrollment>,
    var gender: GenderX,
    var image: String,
    var name: String,
    val parents: List<Parent>,
    var phoneNumber: String,
    val userId: Int
)