package com.example.doancn.Fragments

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.doancn.R
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class UserFillInfoFragment3 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.user_fill_infomation_fragment_3,container,false)
        val input = view.findViewById(R.id.dateborn) as TextInputEditText
        input.setOnClickListener {
            datepicker(context,view)
        }
        return view
    }
}
fun  datepicker (context: Context?,view : View){
    val c = Calendar.getInstance()
    val year =c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val input = view.findViewById(R.id.dateborn) as TextInputEditText

   val datepickerdialog=DatePickerDialog(context!!,android.R.style.Theme_Holo_Light_Dialog_MinWidth, { mview, myear, mmonth, mday ->
       val settext ="$mday"+"/"+"$mmonth"+"/"+"$myear"
       input.setText(settext)
   },year,month,day)
    datepickerdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    datepickerdialog.show()

}