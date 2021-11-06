package com.example.doancn.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Models.Announcement
import com.example.doancn.R

class AnnouncementAdapter(var context: Context, var itemList: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(announcement: Announcement) {
            itemView.findViewById<TextView>(R.id.announcement_sender).text = announcement.Name
            itemView.findViewById<TextView>(R.id.announcement_content).text = announcement.content
            itemView.findViewById<ImageView>(R.id.announcement_circleImageView)
                .setImageResource(R.drawable.ic_baseline_person_24)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.announcement_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}