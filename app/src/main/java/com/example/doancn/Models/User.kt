package com.example.doancn.Models

import java.io.Serializable

class User(
    var image: String = "", var dob: String = "", var educationLevel: String = "",
    var currentWorkPlace: String = "", var address: String = "", var name: String = "",
    var phoneNumber: String = "chưa xác định"
) : Serializable {

}