package com.example.doancn.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.doancn.Fragments.MyClass.homework.SubmissionFragment
import com.example.doancn.Models.SubmissionX
import com.example.doancn.R
import kotlinx.android.synthetic.main.submission_item.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SubmissionAdapter(context: Context,var listsubmission : List<SubmissionX>,val submissionFragment: SubmissionFragment) : ArrayAdapter<SubmissionX>(context,
    R.layout.submission_item ) {

    @SuppressLint("ViewHolder", "InflateParams", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.submission_item, null)
        view.name.text = listsubmission[position].name
        view.by.text=listsubmission[position].student.name
        val index: Int = listsubmission[position].name.lastIndexOf('.')
        val type : String = listsubmission[position].name.substring(index+1)
        if (listsubmission[position].sizeInByte<1024)
            view.size.text="dung lượng: "+listsubmission[position].sizeInByte.toString()+" Byte"
        else
            if (listsubmission[position].sizeInByte>	1048576 )
                view.size.text="dung lượng: "+(listsubmission[position].sizeInByte/1048576).toString()+" Mb"
            else
                view.size.text="dung lượng: "+(listsubmission[position].sizeInByte/1024).toString()+" Kb"

        if (listsubmission[position].late) {
            view.status.text = "Trễ"
            view.status.setTextColor(context.resources.getColor(R.color.red))
        }
        else {
            view.status.text = "Đúng hạn"
            view.status.setTextColor(context.resources.getColor(R.color.green))
        }
        view.time.text= "Nộp lúc: "+listsubmission[position].dateCreated
        view.btn_download.setOnClickListener {
            submissionFragment.viewModel.setSubmissionSelected(listsubmission[position])
            submissionFragment.createFile()
        }
        HomeWorkAdapter.setImageViewByType(type,view)
        return  view
    }

    override fun getCount(): Int {
        return listsubmission.size
    }
}