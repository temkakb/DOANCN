package com.example.doancn.Models

import java.io.Serializable

class User() : Serializable {
    lateinit var phoneNumber: String
    lateinit var name: String
    lateinit var address: String
    lateinit var currentWorkPlace: String
    lateinit var educationLevel: String
    lateinit var dob: String
    var image: String = ""

}