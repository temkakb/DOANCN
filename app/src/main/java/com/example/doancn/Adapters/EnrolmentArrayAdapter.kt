package com.example.doancn.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.doancn.Models.Classroom
import com.example.doancn.R
import kotlinx.android.synthetic.main.class_items.view.*

class EnrolmentArrayAdapter (context: Context,val listclass: List<Classroom>): ArrayAdapter<Classroom>(context,
    R.layout.class_items,listclass) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =  LayoutInflater.from(context).inflate(R.layout.class_items,null)
        val fee = "Phí: "+listclass[position].fee.toString()+" VND"
        val teacher = "Thầy: "+listclass[position].teacher.name
        val subject ="Môn: "+listclass[position].subject.name
        view.teacher.text= teacher
        view.subject.text=subject
        view.classname.text= listclass[position].name
        view.fee.text=fee
        return view
    }


}