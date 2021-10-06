package com.example.doancn.Fragments.LoginSignUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.doancn.R

class UserFillInfoFragment2 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =layoutInflater.inflate(R.layout.user_fill_infomation_fragment_2,container,false)
        val gender = view.findViewById(R.id.gender) as AutoCompleteTextView
        val genderarray =resources.getStringArray(R.array.gender_select)
        gender.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_gender,genderarray))
        return view
    }
}