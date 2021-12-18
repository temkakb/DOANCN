package com.example.doancn.Adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.doancn.Models.Announcement
import com.example.doancn.Models.Classroom
import com.example.doancn.R
import com.example.doancn.Utilities.StringUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AnnouncementAdapter(var context: Context, var classroom: Classroom) :
    RecyclerView.Adapter<AnnouncementAdapter.MyViewHolder>() {
    private var num = 0

    private var announcements: List<Announcement> = classroom.announcements.let { it ->

        num = it.size
        it.sortedByDescending { announcement -> announcement.time }
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(announcement: Announcement) {
            itemView.findViewById<TextView>(R.id.announcement_sender).text = classroom.teacher.name
            itemView.findViewById<TextView>(R.id.announcement_content).text = announcement.content
            val time = LocalDateTime.parse(
                announcement.time,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            )
            itemView.findViewById<TextView>(R.id.time).text =
                "${StringUtils.dowFormatter(time.dayOfWeek.name)}, ${time.hour}h:${time.minute}"
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            val bmp = classroom!!.teacher.image?.let {
                val imgDecode: ByteArray =
                    Base64.getDecoder().decode(it)
                BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
            } ?: ""
            Glide.with(context)
                .asBitmap()
                .load(bmp)
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .error(R.drawable.orther)
                .into(itemView.findViewById<ImageView>(R.id.announcement_circleImageView))


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.announcement_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.setData(announcements[position])
    }

    fun addItem(announcement: Announcement) {
        val arrayList = ArrayList<Announcement>()
        arrayList.addAll(announcements);
        arrayList.add(announcement)
        announcements = arrayList.sortedByDescending { it.time }
        num = announcements.size
        Log.d("announcements", announcements.toString())
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return num
    }
}