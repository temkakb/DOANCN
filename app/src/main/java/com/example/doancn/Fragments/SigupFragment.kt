package com.example.doancn.Fragments

import android.os.Bundle
import android.util.Log
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
        btnprevious.visibility=View.GONE
        dotranscation(youaresingleton)
        btnnext.setOnClickListener {
            val fragment : Fragment?=childFragmentManager.findFragmentById(R.id.sigup_container)
            if(fragment!!::class!=FillinfoFragment::class){ // check fragment end then do stuff
                dotranscation(fillinfosingleton)
            }
            else {
                // do stuff in fragment end
            }
            btnprevious.visibility=View.VISIBLE
        }
        btnprevious.setOnClickListener {
            childFragmentManager.popBackStack()
            Log.d("backstack",childFragmentManager.backStackEntryCount.toString())
            if(childFragmentManager.backStackEntryCount<3){
                btnprevious.visibility=View.GONE
            }
        }
        return  view
    }
    object fillinfosingleton : FillinfoFragment()
    object  youaresingleton : YouAreFragment()
    fun dotranscation(fragment: Fragment){
        val transcation = childFragmentManager.beginTransaction()
        transcation.replace(R.id.sigup_container,fragment)
        transcation.addToBackStack(null)
        transcation.commit()
    }
}