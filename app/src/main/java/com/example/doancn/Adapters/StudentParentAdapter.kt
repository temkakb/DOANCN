package com.example.doancn.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Models.Parent
import com.example.doancn.R
import kotlinx.android.synthetic.main.item_show_student_info_parent.view.*
import kotlinx.android.synthetic.main.show_student_info.view.*

class StudentParentAdapter(context: Context , list : List<Parent>) : BaseAdapter() {

    private val _context = context
    private val listParents = list


    override fun getCount(): Int {
        return listParents.size
    }

    override fun getItem(p0: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(p0: Int): Long {
        return 0L
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater : LayoutInflater = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_show_student_info_parent,null)
        val parent = listParents[p0]
        view.student_info_parent_name.text = parent.name
        view.student_info_parent_adress.text = parent.address
        view.student_info_parent_phone.text = parent.phoneNumber
        return view
    }
}