package com.example.doancn.Fragments.MyClass.homework

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.doancn.Adapters.HomeWorkAdapter
import com.example.doancn.ClassViewModel
import com.example.doancn.DI.DataState
import com.example.doancn.MainViewModel
import com.example.doancn.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.create_homework_dialog.*
import kotlinx.android.synthetic.main.homework_fragment.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeworkFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()
    private val viewModel: HomeworkViewModel by viewModels()
    private val classviewmodel: ClassViewModel by activityViewModels()
    private var homeWorkAdapter : HomeWorkAdapter? =null
    private var filename : String? =null
    private var dateStart: LocalDate? = null
    private var file : ByteArray? = null
    private var date : String? =null
    private var time: String=" 00:00"
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private lateinit var dialog: Dialog
    private lateinit var navcontroller : NavController


    private var uri : Uri? = null
     val PICK_PDF_FILE = 2
    companion object {
        fun newInstance() = HomeworkFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navcontroller = findNavController()
        val view = inflater.inflate(R.layout.homework_fragment, container, false)
        obverseData()
        setDisplayByRole(view)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getData(classviewmodel.classroom.value!!.classId)
        super.onViewCreated(view, savedInstanceState)
    }







    private fun setDisplayByRole(view: View) {
        if (mainViewModel.role.equals("STUDENT")) {
            view.btn_add_homework.visibility = View.GONE

        } else if (mainViewModel.role.equals("TEACHER")) {
            view.btn_add_homework.visibility = View.VISIBLE
            view.btn_add_homework.setOnClickListener { openDialog() }
        }
    }


    private  fun obverseData(){

        lifecycleScope.launchWhenCreated {
            viewModel.homeworks.collect {
                when(it){
                    is DataState.Loading -> {
                        requireView().process.visibility=View.VISIBLE
                    }
                    is DataState.Success ->{

                        if (homeWorkAdapter==null) {
                            Log.d("thanhcong","KO NULL")
                            homeWorkAdapter = HomeWorkAdapter(requireContext(), it.data!!,navcontroller)
                        }else{
                            homeWorkAdapter!!.swapDataSet(it.data!!)
                        }
                        requireView().listview.adapter = homeWorkAdapter
                        requireView().process.visibility=View.GONE

                    }
                    is DataState.Error -> {Toast.makeText(requireContext(),it.data,Toast.LENGTH_LONG).show()}
                }
            }
        }
    }

    private fun openDialog(){
       dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.create_homework_dialog)
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.cancel_button.setOnClickListener {
            dialog.dismiss()

        }
        dialog.file.setOnClickListener {
            openFile()
        }
        dialog.btn_confirm.setOnClickListener {

            if(date==null|| time==null|| file==null ){
                Toast.makeText(requireContext(),"Chưa điền đủ thông tin",Toast.LENGTH_SHORT).show()
            }else
            {

                obversePostHomeWorkStatus()
                val deadline = date+time
                viewModel.postHomeWork(deadline, filename!!, file!!,
                    classviewmodel.classroom.value!!.classId)
            }
        }



        dialog.btn_pick_date.setOnClickListener {
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(constraintsBuilder.build())
                    .setTitleText("Chọn ngày bắt đầu")
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                dateStart =
                    Instant.ofEpochMilli(datePicker.selection!!).atZone(ZoneId.systemDefault())
                        .toLocalDate()
                date=formatter.format(dateStart)
                dialog.txt_datetime.setText(date+time)
            }
            datePicker.show(requireFragmentManager(), "date picker")
        }
        dialog.btn_pick_hour.setOnClickListener {
            if(date==null) Toast.makeText(requireContext(),"Bạn phải chọn ngày trước",Toast.LENGTH_SHORT).show()
            else{
                val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
                val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                        .build()
                picker.show(requireFragmentManager(), "tag")
                picker.addOnPositiveButtonClickListener {
                  if (picker.hour<10) time=" "+"0"+picker.hour
                  else time=" "+picker.hour
                    if(picker.minute<10) time=time+":"+"0"+picker.minute
                    else time=time+":"+picker.minute

                    dialog.txt_datetime.setText(date+time)

                }
            }

        }


        dialog.show()
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

        }

        startActivityForResult(intent, PICK_PDF_FILE)
    }
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == PICK_PDF_FILE
            && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                this.uri=uri
               filename = getFileName(uri)
                file=readBytes(requireContext(),uri)
                dialog.file.text=filename


            }
        }
    }
    private fun readBytes(context: Context, uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }


    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor = requireContext().contentResolver.query(uri, null, null, null, null)!!
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
    private  fun obversePostHomeWorkStatus (){
        lifecycleScope.launchWhenCreated {
            viewModel.postHomeWorkStatus.collect {
                when(it){
                    is  DataState.Loading ->{}
                    is DataState.Success ->{
                        viewModel.getData(classviewmodel.classroom.value!!.classId)
                        Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()
                    }
                    is DataState.Error-> {Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()}
                }
            }
        }

    }


}