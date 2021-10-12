package com.example.doancn.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.doancn.Models.Fakeclass
import com.example.doancn.R
import kotlinx.android.synthetic.main.class_item.view.*

class EnrolmentArrayAdapter (context: Context,val listclass: List<Fakeclass>): ArrayAdapter<Fakeclass>(context,
    R.layout.class_item,listclass) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =  LayoutInflater.from(context).inflate(R.layout.class_item,null)
        view.classname.text=listclass[position].name
        view.teacher.text=listclass[position].teacher
        view.fee.text=listclass[position].fee.toString()
        return view
    }


}