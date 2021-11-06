package com.example.doancn.Fragments.LoginSignUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.doancn.R
import kotlinx.android.synthetic.main.login_fragment.view.*

import kotlinx.android.synthetic.main.user_fill_infomation_fragment_2.view.*

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
        setEventOnTextChange(view)
        return view
    }
    private fun setEventOnTextChange(view: View) {
        view.phone.doOnTextChanged { text, start, before, count ->
            if(text!!.length<9 ) view.phone.error="Số điện thoại từ 9 đến 11 chữ số"
            if(!SigupFragment.validatePhoneNumber(text.toString())) view.phone.error="Số điện thoại chỉ được nhập số"
            else view.phone.error=null
             }
    }
}