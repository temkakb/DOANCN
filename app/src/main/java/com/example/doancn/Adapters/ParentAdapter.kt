package com.example.doancn.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Models.Parent
import com.example.doancn.R
import kotlinx.android.synthetic.main.item_parent.view.*

class ParentAdapter(private val listener: OnItemClickListener)
    : RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

    var myListParent: ArrayList<Parent>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Parent>?) {
        myListParent = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parent, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parent = myListParent?.get(position) ?: return
        holder.tvParentName.text = parent.name
        holder.tvParentPhone.text = parent.phoneNumber
        holder.tvParentAddress.text = parent.address
    }

    override fun getItemCount(): Int {
        return if (myListParent != null) myListParent!!.size else 0
    }


    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tvParentPhone: TextView = itemView.findViewById(R.id.parent_phone)
        val tvParentName: TextView = itemView.findViewById(R.id.parent_name)
        val tvParentAddress : TextView = itemView.findViewById(R.id.parent_adress)

        init {
            itemView.setOnClickListener(this)
            itemView.parent_delete.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            if( p0!!.id == R.id.parent_delete ){
                val position: Int = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRemoveItemClick(position)
                }
            }else {
                val position: Int = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
            }
        }

    interface OnItemClickListener{
        fun onItemClick(position : Int)
        fun onRemoveItemClick(position: Int)
    }
}