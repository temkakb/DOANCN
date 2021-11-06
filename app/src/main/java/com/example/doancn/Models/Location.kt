package com.example.doancn.Models

import java.io.Serializable
import java.math.BigDecimal

data class Location(
    val locationId: Long,
    var address: String,
    var city: String,
    var coordinateX: BigDecimal,
    var coordinateY: BigDecimal
) : Serializable