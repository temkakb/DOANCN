package com.example.doancn.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Fragments.JoinClass.JoinClassFragment
import com.example.doancn.R
import kotlinx.android.synthetic.main.subject_items.view.*

class SubjectsAdapter (val listsubjectname : Array<String>,val fragment: JoinClassFragment): RecyclerView.Adapter<SubjectsAdapter.Sviewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Sviewholder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_items,parent,false)
        return Sviewholder(view,parent.context)
    }
    override fun onBindViewHolder(holder: Sviewholder, position: Int) {
        holder.txt.text=listsubjectname[position]

        if(position==0){
            holder.txt.setOnClickListener {
                holder.txt.startAnimation(AnimationUtils.loadAnimation(holder.context,R.anim.animation_textview_pressed)) // setevent
                fragment.getClassrooms(null) // thuc thi ham get danh sach
            }
        }
        else{
        holder.txt.setOnClickListener {
            holder.txt.startAnimation(
                AnimationUtils.loadAnimation(
                    holder.context,
                    R.anim.animation_textview_pressed
                )
            ) // setevent
            fragment.getClassrooms(position.toLong()) // thuc thi ham get danh sach
        }
        }
    }
    override fun getItemCount(): Int {
        return listsubjectname.size
    }

    inner class Sviewholder ( view : View,val context: Context) : RecyclerView.ViewHolder(view){
        val txt =view.txt_subject

    }




}