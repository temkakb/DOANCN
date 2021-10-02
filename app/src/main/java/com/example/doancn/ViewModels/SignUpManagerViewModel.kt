package com.example.doancn.ViewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.doancn.Models.AccountSignUp
import com.example.doancn.R

class SignUpManagerViewModel(val account : AccountSignUp,val context : Context) : ViewModel() {
    lateinit var rolepick : HashMap<String,Int>
     lateinit var genderpick : HashMap<String,String>
    init {
         rolepick= HashMap()
         genderpick= HashMap()
         val genderarray = context.resources.getStringArray(R.array.gender_select)
         val genderarrayjson = context.resources.getStringArray(R.array.gender_json)
         rolepick.put(context.getString(R.string.teacher),0)
         rolepick.put(context.getString(R.string.student),1)
         var postion =0
         for (gender in genderarray)
         {
              genderpick.put(gender,genderarrayjson[postion++])
         }
    }
}