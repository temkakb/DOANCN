package com.example.doancn.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.doancn.R
import com.example.doancn.ViewModels.SignUpManagerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.log

open class YouAreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.youare_fragment, container, false)
        GlobalScope.launch(Dispatchers.Default) { // tranh ui block khi them data vao account
            val viewmodel = ViewModelProvider(requireActivity()).get(SignUpManagerViewModel::class.java) // call viewmodel in this fragment
            val rg = view.findViewById(R.id.RG_sigup) as RadioGroup
            rg.setOnCheckedChangeListener { radioGroup, i ->
                val checkedradio = view.findViewById(i) as RadioButton
                viewmodel.account.role= viewmodel.rolepick.get(checkedradio.text)!! // not null pls
            }
        }
        return view

    }
}