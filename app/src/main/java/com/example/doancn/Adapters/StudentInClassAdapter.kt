package com.example.doancn.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Fragments.MyClass.people.PeopleViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.PaymentHistory
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import kotlinx.android.synthetic.main.show_payment_history.view.*
import kotlinx.android.synthetic.main.show_student_info.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@ExperimentalCoroutinesApi
class StudentInClassAdapter(
    classroom: Classroom, role: String
    , private val listener: OnItClickListener
    ,peopleViewModel: PeopleViewModel
    ,_token : String
    ,_context: Context
)
    : RecyclerView.Adapter<StudentInClassAdapter.StudentInClassViewHolder>() {
    private val _class = classroom
    private val _role = role
    private var myListUser: List<UserMe>? = null
    private var now : LocalDate = LocalDate.now()
    private val viewModel = peopleViewModel
    private val token = _token
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
        var numberPayment : Int = 0
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
                    numberPayment = i.paymentHistories.count()
                    holder.itemPaymentDate.text = i.nextPaymentAt
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val nextPay = LocalDate.parse(i.nextPaymentAt, formatter)
                    if(now.isAfter(nextPay)){
                        holder.itemStatus.text = R.string.notPay.toString()
                        holder.itemStatus.setBackgroundColor(Color.RED)
                    }
                }

            }
        }

        if(numberPayment > 0) {
            holder.itemStatus.setOnClickListener {
                var total : Double = 0.0
                listener.showPaymentHistory(position)
                if(_role == "TEACHER"){
                    val showPaymnetInfoLayout: View = LayoutInflater.from(myContext)
                        .inflate(R.layout.show_payment_history, null)

                    for (i in student.enrollments!!){
                        if(i.classroom.classId == _class.classId){
                            if(i.paymentHistories.count() > 1)
                                for(a in i.paymentHistories)
                                    total += a.amount
                            else
                                total = i.paymentHistories[0].amount
                            val paymentHistoryAdapter =
                                PayementHistoryAdapter(showPaymnetInfoLayout.context,i.paymentHistories)
                            showPaymnetInfoLayout.list_payment_history.adapter = paymentHistoryAdapter
                        }
                    }
                    showPaymnetInfoLayout.total_amount.text = total.toFloat().toString()
                    val builder = AlertDialog.Builder(myContext)
                    builder.setView(showPaymnetInfoLayout)
                    val dialog = builder.create()
                    dialog.show()
                    showPaymnetInfoLayout.cancel_payment_button.setOnClickListener {
                        dialog.dismiss()
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
                        viewModel.updateStudentPayment(token = token,id = i.enrollmentId,_class.classId)
                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (myListUser != null) myListUser!!.count() else 0
    }

    inner class StudentInClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.student_name)
        var itemPaymentDate: TextView = itemView.findViewById(R.id.student_payment_date)
        var itemStatus: TextView = itemView.findViewById(R.id.student_status)
        var itemPay : TextView = itemView.findViewById(R.id.student_pay)
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
        fun showPaymentHistory(position: Int)
    }
}