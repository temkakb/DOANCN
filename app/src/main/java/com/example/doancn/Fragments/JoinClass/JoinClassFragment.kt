package com.example.doancn.Fragments.JoinClass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.doancn.R
import kotlinx.android.synthetic.main.fragment_joinclass.*
import kotlinx.android.synthetic.main.fragment_joinclass.view.*

class JoinClassFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_joinclass, container, false)
        val layoutmanager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        val subject = view.RC_subjects
//        subject.adapter=SubjectsAdapter(listfakedata)
//        subject.layoutManager=layoutmanager
//        view.joinclass_listview.adapter=EnrolmentArrayAdapter(requireContext(),listfakedata2)
        return view
    }
}
