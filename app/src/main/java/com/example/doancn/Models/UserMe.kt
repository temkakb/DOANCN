package com.example.doancn.Models

import java.io.Serializable


data class UserMe(
    val account: AccountX,
    var address: String,
    val attendances: List<Attendace>,
    var currentWorkPlace: String,
    val dateCreated: String,
    var dob: String,
    var educationLevel: String,
    var enrollments: ArrayList<Enrollment>?,
    var gender: GenderX,
    var image: String,
    var name: String,
    var parents: ArrayList<Parent>,
    var phoneNumber: String,
    val userId: Int,
    var subjectsQualified : List<Subject>,
    var classes : ArrayList<Classroom>?
) : Serializable