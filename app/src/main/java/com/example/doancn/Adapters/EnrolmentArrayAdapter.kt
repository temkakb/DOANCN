package com.example.doancn.Adapters

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.doancn.Models.Classroom
import com.example.doancn.R
import com.example.doancn.Repository.EnrollmentRepository
import kotlinx.android.synthetic.main.class_items.view.*
import kotlinx.android.synthetic.main.detail_classroom_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class EnrolmentArrayAdapter (context: Context,val listclass: List<Classroom>,val token: String): ArrayAdapter<Classroom>(context,
    R.layout.class_items,listclass) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       val  view =  LayoutInflater.from(context).inflate(R.layout.class_items,null)
        val fee = "Phí: "+listclass[position].fee.toString()+"VND"+"/"+listclass[position].option.name
        val teacher = "Thầy: "+listclass[position].teacher.name
        val subject ="Môn: "+listclass[position].subject.name
        val startday ="Ngày bắt đầu: "+listclass[position].startDate
        view.teacher.text= teacher
        view.subject.text=subject
        view.classname.text= listclass[position].name
        view.fee.text=fee
        view.startday.text=startday
        if(listclass[position].enrolled){
            switchtoenrolled(view.btn_enroll)
            setEventButtonEnrollment(view.btn_enroll,listclass[position])
        }
        else{
            switchtoenroll(view.btn_enroll)
            setEventButtonEnrollment(view.btn_enroll,listclass[position]) // set event for button in listclassroom
        }
        setEventForView(view,listclass[position]) // set event for view show dialog
        return view
    }




    private fun switchtoenroll(btn : Button){
        btn.text=context.resources.getString(R.string.sigup)
       btn.background=(ContextCompat.getDrawable(context,R.drawable.bg_button_enroll))
    }

    private fun switchtoenrolled(btn: Button){
        btn.text=context.resources.getString(R.string.enrolled)
        btn.background=(ContextCompat.getDrawable(context,R.drawable.bg_button_enrolled))
    }

    private fun setEventButtonEnrollment (btn: Button,c: Classroom){
        btn.setOnClickListener {
            GlobalScope.launch {
               val doevent= doEnrollOrRemove(btn,c)
                if (doevent){
                    if(it.btn_enroll.text==context.resources.getString(R.string.sigup)){
                        withContext(Dispatchers.Main){ // gan view on main
                            switchtoenrolled(btn)
                        }
                    }
                    else{
                        withContext(Dispatchers.Main){
                            switchtoenroll(btn)
                        }

                    }
                }
            }
        }
    }
    private fun setEventButtonDialog(btn: Button,btnview: Button,c:Classroom){
        btn.setOnClickListener {
            GlobalScope.launch {
                val doevent=   doEnrollOrRemove(btn,c)
                if (doevent){
                    if(btn.btn_enroll.text==context.resources.getString(R.string.sigup)){
                        withContext(Dispatchers.Main){
                            switchtoenrolled(btn)
                            switchtoenrolled(btnview) // switch button affter dialog
                        }
                    }
                    else{
                        withContext(Dispatchers.Main)
                        {
                            switchtoenroll(btn)
                            switchtoenroll(btnview)
                        }
                    }
                }
            }
        }
    }


    private fun setEventForView (view: View,c: Classroom){
        view.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.detail_classroom_dialog)
            dialog.about.text=c.about
            dialog.shortdescription.text=c.shortDescription
            dialog.address.text=c.location.address
            dialog.teacher.text=c.teacher.name
            dialog.classname.text=c.name
            dialog.startday.text=c.startDate
            dialog.numberattendance.text=c.currentAttendanceNumber.toString()
            val fee =c.fee.toString() +"/"+ c.option.name
            dialog.fee.text=fee
            if(c.enrolled){
                switchtoenrolled(dialog.btn_enroll)
                setEventButtonDialog(dialog.btn_enroll,view.btn_enroll,c)


            }
            else{
                switchtoenroll(dialog.btn_enroll)
                setEventButtonDialog(dialog.btn_enroll,view.btn_enroll,c)

            }
            dialog.show()

        }
    }
    suspend fun  doEnrollOrRemove(btn: Button,c: Classroom) : Boolean{
        val enrollmentRepository = EnrollmentRepository()
        if(btn.text.equals(context.getString(R.string.sigup)))
        {
                try{
                enrollmentRepository.doEnroll(c.classId,token)
                c.enrolled=true
                }catch (e: HttpException){
                  if (e.code()==410){
                      withContext(Dispatchers.Main){
                          Toast.makeText(context,"Không thể đăng ký, Khóa học đã bắt đầu",Toast.LENGTH_SHORT).show()
                      }
                  }
                    if(e.code()==400)
                    {
                        withContext(Dispatchers.Main){
                            Toast.makeText(context,"Không tìm thấy lớp học",Toast.LENGTH_SHORT).show()
                        }
                    }
                    if(e.code()==409)
                    {
                        withContext(Dispatchers.Main){
                            Toast.makeText(context,"đã đăng ký khóa học",Toast.LENGTH_SHORT).show()
                        }
                    }
                return false
                }
        }else{
            GlobalScope.launch {
                enrollmentRepository.doDeleteEnrollment(c.classId,token)
                c.enrolled=false
            }
        }
        return true
    }



    }


