package com.example.doancn.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.doancn.Models.AccountSignUp
import com.example.doancn.R

class SignUpManagerViewModel(val account: AccountSignUp,context: Context

) : ViewModel() {
    var rolepick: HashMap<String, Int>
    var genderpick: HashMap<String, String>

    init {
        rolepick = HashMap()
        genderpick = HashMap()
        val genderarray = context.resources.getStringArray(R.array.gender_select)
        val genderpost =context.resources.getStringArray(R.array.gender_post)
        rolepick[context.resources.getString(R.string.teacher)] = 0
        rolepick[context.resources.getString(R.string.student)] = 1
        var postion = 0
        for (gender in genderarray) {
            genderpick[gender] = genderpost[postion++]
        }
    }
}