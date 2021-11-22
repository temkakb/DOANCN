package com.example.doancn.Models

import java.io.Serializable

data class HomeWorkX(
    val dateCreated: String,
    val deadline: String,
    val fileId: Int,
    val name: String,
    val sizeInByte: Int
) : Serializable