package com.example.doancn.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.NavController
import com.example.doancn.Fragments.MyClass.homework.HomeworkFragment
import com.example.doancn.Models.HomeWorkX
import com.example.doancn.R
import kotlinx.android.synthetic.main.homework_item.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class HomeWorkAdapter @ExperimentalCoroutinesApi constructor(context: Context, var listhomework : List<HomeWorkX>, val navController: NavController, val role: String, val homeWorkFragment : HomeworkFragment) : ArrayAdapter<HomeWorkX>(context, R.layout.homework_item) {


    @SuppressLint("ViewHolder", "SetTextI18n", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = LayoutInflater.from(context).inflate(R.layout.homework_item, null)
        view.name.text=listhomework[position].name
        val index: Int = listhomework[position].name.lastIndexOf('.')
        val type : String = listhomework[position].name.substring(index+1)
        setImageViewByType(type,view)
        view.deadline.text="thời hạn: "+listhomework[position].deadline
       if (listhomework[position].sizeInByte<1024)
           view.size.text="dung lượng: "+listhomework[position].sizeInByte.toString()+" Byte"
        else
            if (listhomework[position].sizeInByte>	1048576 )
                view.size.text="dung lượng: "+(listhomework[position].sizeInByte/1048576).toString()+" Mb"
        else
                view.size.text="dung lượng: "+(listhomework[position].sizeInByte/1024).toString()+" Kb"

        if (role.equals("TEACHER")) {
            view.setOnClickListener {
                toSubmission(listhomework[position])
            }
        }
        else if (role.equals("STUDENT")){
            view.setOnClickListener {
                homeWorkFragment.viewModel.selectitem(listhomework[position])
               homeWorkFragment.submissionViewModel.getSubmission(
                   homeWorkFragment.classviewmodel.classroom.value!!.classId,
               listhomework[position].fileId)

            }
        }
        return view
    }

    override fun getCount(): Int {
        return listhomework.size
    }

    fun swapDataSet(homeworks: List<HomeWorkX>) {
        this.listhomework = homeworks
        notifyDataSetChanged()
    }

    companion object {
        fun setImageViewByType(type: String, view: View) {
            if (type.equals("pdf"))
                view.image.setBackgroundResource(R.drawable.pdf)
            else if (type.equals("docx"))
                view.image.setBackgroundResource(R.drawable.word)
            else if (type.equals("xlsx"))
                view.image.setBackgroundResource(R.drawable.excel)
            else if (type.equals("rar"))
                view.image.setBackgroundResource(R.drawable.rar)
            else {
                view.image.setBackgroundResource(R.drawable.ic_baseline_attach_file_24)
            }

        }
    }
 fun toSubmission(homework: HomeWorkX) {
        val bundle = Bundle()
        bundle.putSerializable("targetHomework",homework)
     navController.navigate(R.id.action_navigation_homework_to_submissionFragment,bundle)
    }
}