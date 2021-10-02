package com.example.doancn.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.doancn.R
import com.example.doancn.ViewModels.SignUpManagerViewModel

open class UserFillInfoFragment :Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =layoutInflater.inflate(R.layout.user_fill_infomation_fragment,container,false)
        val viewmodel = ViewModelProvider(requireActivity()).get(SignUpManagerViewModel::class.java)
        return view
    }
}