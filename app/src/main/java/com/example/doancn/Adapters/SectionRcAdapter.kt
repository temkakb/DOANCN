package com.example.doancn.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Models.SectionX
import com.example.doancn.R
import kotlinx.android.synthetic.main.section_item.view.*
import java.lang.ArithmeticException

class SectionRcAdapter (var listsection : List<SectionX>): RecyclerView.Adapter<SectionRcAdapter.Sectionholder>() {
    private lateinit var context : Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Sectionholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.section_item,parent,false)
        context=parent.context
        return Sectionholder(view)
    }

    override fun onBindViewHolder(holder: Sectionholder, position: Int) {
        holder.setData(listsection[position])
    }

    override fun getItemCount(): Int {
       return listsection.size
    }
    inner class Sectionholder( itemview :View) : RecyclerView.ViewHolder(itemview){
        val monhth = itemview.month
        val day = itemview.day
        val attendancednumbers =itemview.attendancednumbers
        val nonattendancednumbers = itemview.nonattendancednumbers
        fun setData(sectionX: SectionX){
            val numbersdiemdanh="Điểm danh: "+ sectionX.attendancednumbers
            val numbersvang = "Vắng: "+sectionX.nonattendancednumbers
            var percent: Float
            if(sectionX.attendancednumbers + sectionX.nonattendancednumbers==0)
            percent=0F
            else percent = (sectionX.nonattendancednumbers.toFloat() / (sectionX.attendancednumbers + sectionX.nonattendancednumbers) * 100)
            monhth.text=sectionX.dateCreated.substring(5,7)
            day.text=sectionX.dateCreated.substring(8,10)
            attendancednumbers.text= numbersdiemdanh
            nonattendancednumbers.text= numbersvang
            if(percent>50 ) itemView.color.background=ContextCompat.getDrawable(context,R.drawable.bg_dangerous_section)
            else if (percent>10) itemView.color.background=ContextCompat.getDrawable(context,R.drawable.bg_warning_section)
            else itemView.color.background=ContextCompat.getDrawable(context,R.drawable.bg_safe_section)
        }
    }
}