package com.example.doancn.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.doancn.R

class SigupFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.signup_fragment,container,false)
        val btnnext =view.findViewById(R.id.btn_next) as Button
        val btnprevious = view.findViewById(R.id.btn_previous) as Button
        dotranscation(YouAreFragment())
        btnnext.setOnClickListener {
         dotranscation(FillinfoFragment())
        }
        btnprevious.setOnClickListener {
            childFragmentManager.popBackStack()
        }
        return  view
    }
    fun dotranscation(fragment: Fragment){
        val transcation = childFragmentManager.beginTransaction()
        transcation.replace(R.id.sigup_container,fragment)
        transcation.addToBackStack(null)
        transcation.commit()
    }
}