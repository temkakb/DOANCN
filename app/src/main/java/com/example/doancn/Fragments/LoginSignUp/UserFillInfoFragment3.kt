package com.example.doancn.Fragments.LoginSignUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.doancn.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class UserFillInfoFragment3 : Fragment() {
    private var dateStart: LocalDate? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            layoutInflater.inflate(R.layout.user_fill_infomation_fragment_3, container, false)
        GlobalScope.launch(Dispatchers.Default) {
            val input = view.findViewById(R.id.dateborn) as TextInputEditText
            input.setOnClickListener {
                val input = view.findViewById(R.id.dateborn) as TextInputEditText
                val constraintsBuilder =
                    CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointBackward.now())
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .setCalendarConstraints(constraintsBuilder.build())
                        .setTitleText("Chọn ngày bắt đầu")
                        .build()
                datePicker.addOnPositiveButtonClickListener {
                    dateStart =
                        Instant.ofEpochMilli(datePicker.selection!!).atZone(ZoneId.systemDefault())
                            .toLocalDate()
//                val date = dateStart
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formattedString: String = formatter.format(dateStart)
                    input.setText(formattedString)
                }

                datePicker.show(requireFragmentManager(), "date picker")
            }
        }
        return view
    }
}
