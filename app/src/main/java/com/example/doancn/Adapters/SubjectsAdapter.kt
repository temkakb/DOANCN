//package com.example.doancn.Adapters
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AnimationUtils
//import androidx.recyclerview.widget.RecyclerView
//import com.example.doancn.R
//import kotlinx.android.synthetic.main.subject_items.view.*
//
//class SubjectsAdapter (var listfakesubject : List<FakeSubject>): RecyclerView.Adapter<SubjectsAdapter.Sviewholder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Sviewholder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_items,parent,false)
//        return Sviewholder(view,parent.context)
//    }
//
//    override fun onBindViewHolder(holder: Sviewholder, position: Int) {
//        holder.txt.text=listfakesubject[position].name
//        holder.txt.setOnClickListener {
//            holder.txt.startAnimation(AnimationUtils.loadAnimation(holder.context,R.anim.animation_textview_pressed))
//
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return listfakesubject.size
//    }
//
//    class Sviewholder ( view : View,val context: Context) : RecyclerView.ViewHolder(view){
//        val txt =view.txt_subject
//
//    }
//}