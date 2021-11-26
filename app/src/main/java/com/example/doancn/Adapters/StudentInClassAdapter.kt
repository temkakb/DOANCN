package com.example.doancn.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


@ExperimentalCoroutinesApi
class StudentInClassAdapter(
    classroom: Classroom, role: String
    , private val listener: OnItClickListener
    ,_context: Context
)
    : RecyclerView.Adapter<StudentInClassAdapter.StudentInClassViewHolder>() {
    private val _class = classroom
    private val _role = role
    private var myListUser: List<UserMe>? = null
    private var now : LocalDate = LocalDate.now()
    private val myContext = _context


    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<UserMe>?) {
        val listStudent : ArrayList<UserMe> = ArrayList()
        if (list != null) {
            for(stu in list)
                for (enr in stu.enrollments!!)
                    if(enr.classroom.classId == _class.classId && enr.accepted)
                        listStudent.add(stu)
        }
        myListUser = listStudent
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentInClassViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.people_item,parent,false)
        return StudentInClassViewHolder(v)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: StudentInClassViewHolder, position: Int) {
        val student = myListUser?.get(position) ?: return
        holder.itemName.text = student.name
        if(student.image != null){
            val imgDecode: ByteArray = Base64.getDecoder().decode(student.image)
            val bmp = BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
            holder.itemImage.setImageBitmap(bmp)
        }
        else
        {
            when(student.gender.genderID)
            {
                1 -> { holder.itemImage.setImageResource(R.drawable.man) }
                2 -> { holder.itemImage.setImageResource(R.drawable.femal) }
                3 -> { holder.itemImage.setImageResource(R.drawable.orther) }
            }
        }
        if(_role == "STUDENT" || _class.option.paymentOptionId == 6L){
            //holder.itemPaymentDate.visibility = View.GONE
            holder.itemStatus.visibility = View.GONE
            //holder.itemPay.visibility = View.GONE
        }else if(_role == "TEACHER"){
            for (i in student.enrollments!!)
            {
                if(i.classroom.classId == _class.classId)
                {
                    //holder.itemPaymentDate.text = i.nextPaymentAt
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val nextPay = LocalDate.parse(i.nextPaymentAt, formatter)
                    if(now.isAfter(nextPay)){
                        holder.itemStatus.text = myContext.getString(R.string.notPay)
                        holder.itemStatus.setBackgroundColor(Color.RED)
                    }else{
                        holder.itemStatus.text = myContext.getString(R.string.paid)
                        holder.itemStatus.setBackgroundColor(myContext.getColor(R.color.green))
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return if (myListUser != null) myListUser!!.count() else 0
    }

    inner class StudentInClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.student_name)
        var itemStatus: TextView = itemView.findViewById(R.id.student_status)
        var itemImage : CircleImageView = itemView.findViewById(R.id.student_image)
        init {
            itemView.setOnClickListener {
                val position: Int = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }
    }
    interface OnItClickListener{
        fun onItemClick(position : Int)
    }
}