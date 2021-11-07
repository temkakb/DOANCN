package com.example.doancn.Adapters;

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.doancn.IMainActivity
import com.example.doancn.Models.Classroom
import com.example.doancn.R

class ListClassAdapter(var context: Context, var classList: List<Classroom>) :
    RecyclerView.Adapter<ListClassAdapter.MyViewHolder>() {

    private lateinit var iMainActivity: IMainActivity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.d("onCreateViewHolder", classList.toString())
        val view = LayoutInflater.from(context).inflate(R.layout.class_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("onBindViewHolder", classList[position].toString())
        if (itemCount > 0) {
            holder.setData(classList[position])
        }
    }

    override fun getItemCount(): Int {
        return classList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        iMainActivity = context as IMainActivity
        super.onAttachedToRecyclerView(recyclerView)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profile_image)
        val className: TextView = itemView.findViewById(R.id.class_name)
        val teacherName: TextView = itemView.findViewById(R.id.teacher_name)
        val numberOfAttendances: TextView = itemView.findViewById(R.id.number_of_attendance)

        @SuppressLint("SetTextI18n")
        fun setData(classroom: Classroom) {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
//            Glide.with(context)
//                .load(classroom.teacher.image)
//                .centerCrop()
//                .placeholder(circularProgressDrawable)
//                .error(R.drawable.ic_launcher_background)
//                .into(imageView)
            className.text = classroom.name
            teacherName.text = classroom.teacher.name
            numberOfAttendances.text = "${classroom.currentAttendanceNumber} đã đăng kí tham gia."
            itemView.setOnClickListener {
                iMainActivity.toClass(classroom)
            }
        }


    }
}
