package com.example.doancn.Adapters

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Fragments.MyClass.people.PeopleFragment
import com.example.doancn.Fragments.MyClass.people.PeopleViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import com.example.doancn.Repository.UserRepository
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import java.time.format.DateTimeFormatter




@ExperimentalCoroutinesApi
class StudentInClassAdapter(
    classroom: Classroom, role: String
    , private val listener: OnItClickListener
    ,peopleViewModel: PeopleViewModel
    ,token : String
)
    : RecyclerView.Adapter<StudentInClassAdapter.StudentInClassViewHolder>() {
    private val _class = classroom
    private val _role = role
    var myListUser: List<UserMe>? = null
    private var now : LocalDate = LocalDate.now()
    private val viewModel = peopleViewModel
    private val _token = token


    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<UserMe>?) {
        if (list != null) {
            myListUser = list
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentInClassViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.people_item,parent,false)
        return StudentInClassViewHolder(v)
    }

    override fun onBindViewHolder(holder: StudentInClassViewHolder, position: Int) {
        val student = myListUser?.get(position) ?: return
        holder.itemName.text = student.name
        if(_role == "STUDENT" || _class.option.paymentOptionId == 6L){
            holder.itemPaymentDate.visibility = View.GONE
            holder.itemStatus.visibility = View.GONE
            holder.itemPay.visibility = View.GONE
        }else if(_role == "TEACHER"){

            for (i in student.enrollments!!)
            {
                if(i.classroom.classId == _class.classId)
                {
                    holder.itemPaymentDate.text = i.nextPaymentAt
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val nextPay = LocalDate.parse(i.nextPaymentAt, formatter)
                    if(now.isAfter(nextPay)){
                        holder.itemStatus.text = "Chưa đóng tiền"
                        holder.itemStatus.setBackgroundColor(Color.RED)
                    }
                }
            }
        }

        holder.itemPay.setOnClickListener{
            if(_role == "TEACHER"){
                val formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                for(i in student.enrollments!!){
                    if(i.classroom.classId == _class.classId){
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val nextPay = LocalDate.parse(i.nextPaymentAt, formatter)
                        when(_class.option.paymentOptionId){
                            1L -> {
                                holder.itemPaymentDate.text = nextPay.plusWeeks(1L).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusWeeks(1L).format(formatters).toString()
                            }

                            2L -> {
                                holder.itemPaymentDate.text = nextPay.plusMonths(1L).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusMonths(1L).format(formatters).toString()
                            }

                            3L -> {
                                holder.itemPaymentDate.text = nextPay.plusMonths(3L).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusMonths(3L).format(formatters).toString()
                            }

                            4L -> {
                                holder.itemPaymentDate.text = nextPay.plusYears(1L).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusYears(1L).format(formatters).toString()
                            }

                            5L -> {
                                holder.itemPaymentDate.text = nextPay.plusYears(20000).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusYears(20000).format(formatters).toString()
                            }
                        }
                        viewModel.updateStudentPayment(_token, i.enrollmentId)
                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (myListUser != null) myListUser!!.size else 0
    }

    inner class StudentInClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.student_name)
        var itemPaymentDate: TextView = itemView.findViewById(R.id.student_payment_date)
        var itemStatus: TextView = itemView.findViewById(R.id.student_status)
        var itemPay : TextView = itemView.findViewById(R.id.student_pay)
        init {
            itemView.setOnClickListener {
                /*if (it!!.id == R.id.student_pay) {
                    val position: Int = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        listener.onPayItemClick(position)
                }else {*/
                    val position: Int = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position)
                }
            }
            itemPay.setOnClickListener{
                val position: Int = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.onPayItemClick(position)

            }
        }
    }
    interface OnItClickListener{
        fun onItemClick(position : Int)
        fun onPayItemClick(position: Int)
    }
}