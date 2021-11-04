package com.example.doancn.Models.classModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ClassQuest(
    val about: String,
    val currentAttendanceNumber: Int,
    val fee: Double,
    val location: Location,
    val name: String,
    val paymentOption: PaymentOption,
    val shifts: List<Shift>,
    val shortDescription: String,
    val startDate: String,
    val subject: Subject
) : Parcelable {
    @Parcelize
    data class Location(
        var address: String,
        var city: String,
        var coordinateX: Double,
        var coordinateY: Double
    ) : Parcelable

    @Parcelize
    data class PaymentOption(
        val name: String,
        val paymentOptionId: String
    ) : Parcelable

    @Parcelize
    data class Shift(
        val dayOfWeek: String,
        val duration: String,
        val startAt: String
    ) : Parcelable

    @Parcelize
    data class Subject(
        val name: String,
        val subjectId: String
    ) : Parcelable

    @Parcelize
    data class DayOfWeeks(val name: String) : Parcelable
}

