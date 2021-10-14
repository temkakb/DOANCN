package com.example.doancn.Adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.doancn.Models.Classroom
import com.example.doancn.R
import kotlinx.android.synthetic.main.class_items.view.*
import kotlinx.android.synthetic.main.detail_classroom_dialog.*

class EnrolmentArrayAdapter (context: Context,val listclass: List<Classroom>): ArrayAdapter<Classroom>(context,
    R.layout.class_items,listclass) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       val  view =  LayoutInflater.from(context).inflate(R.layout.class_items,null)
        val fee = "Phí: "+listclass[position].fee.toString()+" VND"
        val teacher = "Thầy: "+listclass[position].teacher.name
        val subject ="Môn: "+listclass[position].subject.name
        view.teacher.text= teacher
        view.subject.text=subject
        view.classname.text= listclass[position].name
        view.fee.text=fee
        if(listclass[position].enrolled){
            switchtoenrolled(view.btn_enroll)
            setEventButtonEnrollment(view.btn_enroll)
        }
        else{
            switchtoenroll(view.btn_enroll)
            setEventButtonEnrollment(view.btn_enroll)
        }
        setEventForView(view,listclass[position])
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

    private fun setEventButtonEnrollment (btn: Button){
        btn.setOnClickListener {
            if(it.btn_enroll.text==context.resources.getString(R.string.sigup)){
                switchtoenrolled(btn)
            }
            else{
                switchtoenroll(btn)
            }
        }
    }
    private fun setEventButtonDialog(btn: Button,btnview: Button){
        btn.setOnClickListener {
            if(btn.btn_enroll.text==context.resources.getString(R.string.sigup)){
                switchtoenrolled(btn)
                switchtoenrolled(btnview)
            }
            else{
                switchtoenroll(btn)
                switchtoenroll(btnview)
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
                setEventButtonDialog(dialog.btn_enroll,view.btn_enroll)


            }
            else{
                switchtoenroll(dialog.btn_enroll)
                setEventButtonDialog(dialog.btn_enroll,view.btn_enroll)

            }
            dialog.show()

        }
    }



    }


