package com.example.doancn.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.doancn.Models.HomeWorkX
import com.example.doancn.R
import kotlinx.android.synthetic.main.homework_item.view.*

class HomeWorkAdapter(context: Context,var listhomework : List<HomeWorkX>) : ArrayAdapter<HomeWorkX>(context, R.layout.homework_item) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.homework_item, null)
        view.name.text=listhomework[position].name
        view.deadline.text="thời hạn: "+listhomework[position].deadline
       if (listhomework[position].sizeInByte<1024)
           view.size.text="dung lượng: "+listhomework[position].sizeInByte.toString()+" Byte"
        else
            if (listhomework[position].sizeInByte>	1048576 )
                view.size.text="dung lượng: "+(listhomework[position].sizeInByte/1048576).toString()+" Mb"
        else
                view.size.text="dung lượng: "+(listhomework[position].sizeInByte/1024).toString()+" Kb"

        return view
    }

    override fun getCount(): Int {
        return listhomework.size
    }

    fun swapDataSet(homeworks: List<HomeWorkX>) {
        this.listhomework = homeworks
        notifyDataSetChanged()
    }
}