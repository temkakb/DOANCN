package com.example.doancn.Utilities

import java.util.regex.Pattern


object StringUtils {
    var map: Map<String, String> = hashMapOf(
        Pair("MONDAY", "Thứ hai"),
        Pair("TUESDAY", "Thứ ba"),
        Pair("WEDNESDAY", "Thứ tư"),
        Pair("THURSDAY", "Thứ năm"),
        Pair("FRIDAY", "Thứ sáu"),
        Pair("SATURDAY", "Thứ bảy"),
        Pair("SUNDAY", "Chủ nhật"),
    )

    fun checkFormat(string: CharSequence?): Int {
        if (string == null || string.trim().isEmpty()) {
            return 0
        }
//        val letter = Pattern.compile("[a-zA-z]")
//        val digit = Pattern.compile("[0-9]")
        val special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]")
        // val p = Pattern.compile("[^A-Za-z0-9]")
        val m = special.matcher(string)

        val b = m.find()

        return if (b) 1 else 2


    }

    fun dowFormatter(dow: String) = map[dow]!!


}

