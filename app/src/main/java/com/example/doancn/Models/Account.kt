package com.example.doancn.Models

open class Account() {
    lateinit var email: String
    lateinit var password: String
    constructor(email: String, password: String) : this() {
        this.email = email
        this.password = password
    }
}