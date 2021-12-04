package com.example.doancn.Adapters

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.doancn.Fragments.JoinClass.JoinClassViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.R
import kotlinx.android.synthetic.main.class_items.view.*
import kotlinx.android.synthetic.main.detail_classroom_dialog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

// noi that vong thau troi xanh
// clean code : #2
@ExperimentalCoroutinesApi
class EnrolmentArrayAdapter(
    context: Context,
    var listclass: List<Classroom>,
    val listsubject: Array<String>,
    val optionarraystring: Array<String>,
    val joinClassViewModel: JoinClassViewModel
) : ArrayAdapter<Classroom>(
    context,
    R.layout.class_items, listclass
) {
    val circularProgressDrawable = CircularProgressDrawable(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater
            .from(context).inflate(R.layout.class_items, null)
        val fee: String
        if (listclass[position].option.paymentOptionId.toInt() == 6) {
            fee = "Miễn phí"
        } else {
            fee =
                "Phí: " + listclass[position].fee.toString() + "VND" + "/" + optionarraystring[listclass[position].option.paymentOptionId.toInt() - 1]
        }
        val subject = "Môn: " + listsubject[listclass[position].subject.subjectId.toInt() - 1]
        val teacher = "Thầy: " + listclass[position].teacher.name
        val startday = "Ngày bắt đầu: " + listclass[position].startDate
        view.teacher.text = teacher
        view.subject.text = subject
        view.classname.text = listclass[position].name
        view.fee.text = fee
        view.startday.text = startday
        val bmp = listclass[position].teacher.image?.let {
            val imgDecode: ByteArray =
                Base64.getDecoder().decode(it)
            BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
        } ?: ""

        Glide.with(context)
            .asBitmap()
            .load(bmp)
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .error(R.drawable.orther)
            .into(view.avatarteacher)
        if (listclass[position].enrolled)
            switchtoenrolled(view.btn_enroll)
        else
            switchtoenroll(view.btn_enroll)
        setEventButtonEnrollment(view, listclass[position])
        setEventForView(view, listclass[position]) // set event for view show dialog
        return view
    }


    override fun getCount(): Int {
        return listclass.size
    }

    private fun switchtoenroll(btn: Button) {
        btn.text = context.resources.getString(R.string.sigup)
        btn.background = (ContextCompat.getDrawable(context, R.drawable.bg_button_enroll))
    }

    private fun switchtoenrolled(btn: Button) {
        btn.text = context.resources.getString(R.string.enrolled)
        btn.background = (ContextCompat.getDrawable(context, R.drawable.bg_button_enrolled))
    }

    private fun setEventButtonEnrollment(view: View, classroom: Classroom) {
        view.btn_enroll.setOnClickListener {
            doEnrollOrRemove(view, classroom)
        }
    }

    private fun setEventButtonDialog(viewdialog: View, c: Classroom, viewitem: View) {
        joinClassViewModel.btnview = viewitem.btn_enroll
        viewdialog.btn_enroll.setOnClickListener {
            doEnrollOrRemove(viewdialog, c)
        }
    }

    private fun setEventForView(view: View, c: Classroom) {
        view.setOnClickListener {
            val viewdialog =
                LayoutInflater.from(context).inflate(R.layout.detail_classroom_dialog, null)
            val dialog = Dialog(context)
            dialog.setContentView(viewdialog)
            dialog.about.text = c.shortDescription
            dialog.shortdescription.text = c.about
            dialog.address.text = c.location.address
            dialog.teacher.text = c.teacher.name
            dialog.classname.text = c.name
            dialog.startday.text = c.startDate
            dialog.numberattendance.text = c.currentAttendanceNumber.toString()
            val bmp = c.teacher.image?.let {
                val imgDecode: ByteArray =
                    Base64.getDecoder().decode(it)
                BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
            } ?: ""

            Glide.with(context)
                .asBitmap()
                .load(bmp)
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .error(R.drawable.orther)
                .into(dialog.avatarteacher)
            val fee: String
            if (c.option.paymentOptionId.toInt() == 6) {
                fee = "Miễn phí"
            } else {
                fee =
                    c.fee.toString() + "VND" + "\n" + optionarraystring[c.option.paymentOptionId.toInt() - 1]
            }
            dialog.fee.text = fee
            if (c.enrolled)
                switchtoenrolled(dialog.btn_enroll)
            else
                switchtoenroll(dialog.btn_enroll)

            dialog.cancel_button.setOnClickListener {
                joinClassViewModel.btnview = null
                dialog.dismiss()
            }
            setEventButtonDialog(viewdialog, c, view)
            dialog.show()
        }
    }


    private fun doEnrollOrRemove(view: View, c: Classroom) {
        joinClassViewModel.view = view
        if (!c.enrolled)
            joinClassViewModel.doEnroll(c)
        else
            joinClassViewModel.doDeleteEnrollment(c)

    }

    fun swapDataSet(listclass: List<Classroom>) {
        this.listclass = listclass
        notifyDataSetChanged()
    }


}


