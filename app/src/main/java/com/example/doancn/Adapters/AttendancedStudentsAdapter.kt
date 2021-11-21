package com.example.doancn.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import kotlinx.android.synthetic.main.item_attendanced_student.view.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.util.*

class AttendancedStudentsAdapter(var context: Context, list : List<UserMe>) : BaseAdapter() {

    private val _context = context
    private val listStudents = list

    override fun getCount(): Int {
        return listStudents.size
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
        val view = inflater.inflate(R.layout.item_attendanced_student,null)
        val student = listStudents[p0]
        if(student!!.image != null){
            val imgDecode: ByteArray = Base64.getDecoder().decode(student.image)
            val bmp = BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
            view.attendanced_student_image.setImageBitmap(bmp)
        }
        else
        {
            when(student.gender.genderID)
            {
                1 -> { view.attendanced_student_image.setImageResource(R.drawable.man) }
                2 -> { view.attendanced_student_image.setImageResource(R.drawable.femal) }
                3 -> { view.attendanced_student_image.user_image.setImageResource(R.drawable.orther) }
            }
        }
        view.attendanced_student_name.text = student.name
        return view
    }
}