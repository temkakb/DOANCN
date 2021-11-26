package com.example.doancn.Adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.doancn.Models.User
import com.example.doancn.R

class ListStudentAdapter(var context: Context, var studentList: List<User>) :
    RecyclerView.Adapter<ListStudentAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profile_image)
        val studentName: TextView = itemView.findViewById(R.id.studentName)
        val optionButton: TextView = itemView.findViewById(R.id.buttonOption)

        fun setData(user: User) {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
//            if (user.image.isNotEmpty()) {
//                circularProgressDrawable.start()
//                Glide.with(context)
//                    .load(user.image)
//                    .centerCrop()
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: Drawable?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            dataSource: DataSource?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            return false
//                        }
//
//                    })
//                    .placeholder(circularProgressDrawable)
//                    .error(R.drawable.ic_launcher_background)
//                    .into(imageView)
//            }
            studentName.text = user.name
            optionButton.setOnClickListener {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
                val popup = PopupMenu(
                    context,
                    itemView,
                    Gravity.RIGHT,
                    R.attr.actionOverflowMenuStyle,
                    0
                )

                popup.inflate(R.menu.student_options_menu)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.option1 -> {
                            Toast.makeText(context, "option1", Toast.LENGTH_SHORT).show()
                            return@setOnMenuItemClickListener true
                        }
                        R.id.option2 -> {
                            Toast.makeText(context, "option2", Toast.LENGTH_SHORT).show()
                            return@setOnMenuItemClickListener true
                        }

                        else -> return@setOnMenuItemClickListener true
                    }
                }
                popup.show()

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.class_student_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(studentList[position])
    }

    override fun getItemCount(): Int {
        return studentList.size
    }
}
