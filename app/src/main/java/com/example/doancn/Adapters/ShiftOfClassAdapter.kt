package com.example.doancn.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.doancn.Models.Classroom
import com.example.doancn.R

class ShiftOfClassAdapter(context: Context,  objects: ArrayList<Classroom>,selected:String,listSubjectName:Array<String>,) :
    ArrayAdapter<Classroom?>(context, 0, objects as MutableList<Classroom?>) {
    private val listNameOfSubject = listSubjectName
    private val time : String = selected

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val o = 0
        val olong : Long = o.toLong()
        val classroom : Classroom? = getItem(position)
        var convertView = convertView
        if (convertView == null)
            convertView  = LayoutInflater.from(context)
            .inflate(R.layout.today_class_items,parent, false)
        val className : TextView = convertView!!.findViewById(R.id.classname)
        val startTime : TextView = convertView.findViewById(R.id.shift_time)
        val teacherName : TextView = convertView.findViewById(R.id.shift_teacher)
        val subjectName : TextView = convertView.findViewById(R.id.shift_subject)
        var timestart : String
        val teacher: String = "Thầy/Cô:"+classroom!!.teacher.name
        val subjectN: String = "Môn: "+listNameOfSubject[classroom.subject.subjectId.toInt()]
        className.text = classroom.name
        teacherName.text = teacher
        subjectName.text = subjectN
        for ( shift in classroom.shifts){
            if(shift.dayOfWeek.dowName == time){
                if (shift.startAt.minus((shift.startAt.div(3600000))
                        .times(3600000)) != olong)
                    if((shift.startAt.minus((shift.startAt.div(3600000))
                            .times(3600000))).div(60000).toInt() < 10){
                        timestart = shift.startAt.div(3600000).toString() +
                                ":0" + (shift.startAt.minus((shift.startAt.div(3600000))
                            .times(3600000))).div(60000).toString()
                    }else
                        timestart = shift.startAt.div(3600000).toString() +
                                ":" + (shift.startAt.minus((shift.startAt.div(3600000))
                            .times(3600000))).div(60000).toString()
                else
                    timestart = shift.startAt.div(3600000).toString() + ":00"
                startTime.text = timestart
            }else
            {
                continue
            }
        }
        return convertView
    }

}