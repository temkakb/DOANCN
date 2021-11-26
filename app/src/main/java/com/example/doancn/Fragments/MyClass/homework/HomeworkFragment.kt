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
import com.example.doancn.Models.SubmissionX
import com.example.doancn.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.create_homework_dialog.*
import kotlinx.android.synthetic.main.create_homework_dialog.cancel_button
import kotlinx.android.synthetic.main.create_homework_dialog.file
import kotlinx.android.synthetic.main.homework_fragment.view.*
import kotlinx.android.synthetic.main.homework_item.view.*
import kotlinx.android.synthetic.main.submission_dialog.*
import kotlinx.android.synthetic.main.submission_item.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter








@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeworkFragment : Fragment() {
    val mainViewModel: MainViewModel by viewModels()
    val viewModel: HomeworkViewModel by viewModels()
    val classviewmodel: ClassViewModel by activityViewModels()
    val submissionViewModel: SubmissionViewModel by viewModels()
    val TEACHER_CREATE_FILE = 1
    val STUDENT_CREATE_FILE = 3
    private var homeWorkAdapter: HomeWorkAdapter? = null
    private var filename: String? = null
    private var dateStart: LocalDate? = null
    private var file: ByteArray? = null
    private var date: String? = null
    private var time: String = " 00:00"
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private lateinit var dialog: Dialog
    private lateinit var navcontroller: NavController
    private  val number =1000*1000;


    private var uri: Uri? = null
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
        obverseSubmission()
        setDisplayByRole(view)
        obversePostHomeWorkStatus()
        obverseStatusRequest()
        obverseDownloadStatus()

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


    private fun obverseData() {

        lifecycleScope.launchWhenCreated {
            viewModel.homeworks.collect {
                when (it) {
                    is DataState.Loading -> {
                        requireView().process.visibility = View.VISIBLE
                    }
                    is DataState.Success -> {

                        if (homeWorkAdapter == null) {
                            homeWorkAdapter = HomeWorkAdapter(
                                requireContext(),
                                it.data!!,
                                navcontroller,
                                mainViewModel.role!!,
                                this@HomeworkFragment
                            )
                        } else {
                            homeWorkAdapter!!.swapDataSet(it.data!!)
                        }
                        requireView().listview.adapter = homeWorkAdapter
                        requireView().process.visibility = View.GONE

                    }
                    is DataState.Error -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.create_homework_dialog)
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.cancel_button.setOnClickListener {
            clearData()
            dialog.dismiss()
        }
        dialog.file.setOnClickListener {
            openFile()
        }
        dialog.btn_confirm.setOnClickListener {

            if (date == null || file == null) {
                Toast.makeText(requireContext(), "Chưa điền đủ thông tin", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val deadline = date + time
                viewModel.postHomeWork(
                    deadline, filename!!, file!!,
                    classviewmodel.classroom.value!!.classId
                )
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
                date = formatter.format(dateStart)
                dialog.txt_datetime.setText(date + time)
            }
            datePicker.show(requireFragmentManager(), "date picker")
        }
        dialog.btn_pick_hour.setOnClickListener {
            if (date == null) Toast.makeText(
                requireContext(),
                "Bạn phải chọn ngày trước",
                Toast.LENGTH_SHORT
            ).show()
            else {
                val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
                val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                        .build()
                picker.show(requireFragmentManager(), "tag")
                picker.addOnPositiveButtonClickListener {
                    if (picker.hour < 10) time = " " + "0" + picker.hour
                    else time = " " + picker.hour
                    if (picker.minute < 10) time = time + ":" + "0" + picker.minute
                    else time = time + ":" + picker.minute

                    dialog.txt_datetime.setText(date + time)

                }
            }

        }


        dialog.show()
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/*"


        }
        startActivityForResult(intent, PICK_PDF_FILE)
    }
    fun createFile() {
        val rnds = (0..number).random()
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, rnds.toString()+"-"+viewModel.homeWork.value!!.name)
        }
        if (mainViewModel.role.equals("STUDENT")) {
            startActivityForResult(intent, STUDENT_CREATE_FILE)
        }else if (mainViewModel.role.equals("TEACHER")) {
            startActivityForResult(intent, TEACHER_CREATE_FILE)
        }

    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?
    ) {
        if (requestCode == PICK_PDF_FILE
            && resultCode == Activity.RESULT_OK
        ) {
            resultData?.data?.also { uri ->
                this.uri = uri
                filename = getFileName(uri)
                file = readBytes(requireContext(), uri)

                try {
                    dialog.filename.setText(filename)
                } catch (ex: NullPointerException) {

                }
                try {
                    dialog.filename2.setText(filename)
                } catch (ex: NullPointerException) {
                }


            }
        }
        else if (requestCode==TEACHER_CREATE_FILE && resultCode == Activity.RESULT_OK){
            resultData?.data?.also { uri ->
             viewModel.teacherDownLoadHomeWork(classviewmodel.classroom.value!!.classId,uri,requireContext())
            }
        }
        else if(requestCode==STUDENT_CREATE_FILE && resultCode==Activity.RESULT_OK){
            resultData?.data?.also { uri ->
            viewModel.studentDownLoadHomeWork(classviewmodel.classroom.value!!.classId,uri,requireContext())
            }
        }

    }

    private fun clearData() {

        filename = null
        dateStart = null
        file  = null
        date  = null
        time = " 00:00"
    }

    private fun readBytes(context: Context, uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }


    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor =
                requireContext().contentResolver.query(uri, null, null, null, null)!!
            try {
                if (cursor.moveToFirst()) {
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

    private fun obverseDownloadStatus (){
        lifecycleScope.launchWhenCreated {
            viewModel.downloadStatus.collect {
                when(it) {
                    is DataState.Loading -> {
                    viewModel.view.value!!.process_homework_item.visibility=View.VISIBLE
                    }
                    is DataState.Success-> {
                        viewModel.view.value!!.process_homework_item.visibility=View.GONE
                        Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()
                        viewModel.setEmptyDownLoadStatus()
                    }
                    is  DataState.Error-> {
                        viewModel.view.value!!.process_homework_item.visibility=View.GONE
                        Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()
                        viewModel.setEmptyDownLoadStatus()
                    }
                }
            }
        }
    }
    private fun obverseStatusRequest(){
        lifecycleScope.launchWhenCreated {
            submissionViewModel.statusRequest.collect {
                when(it){
                    is DataState.Loading->{
                        dialog.process_submission.visibility= View.VISIBLE
                        dialog.btn_confirm_or_cancel.text =resources.getString(R.string.pls_wait)
                    }
                    is DataState.Success-> {
                        dialog.dismiss()
                        clearData()
                        submissionViewModel.setEmptyStatus()
                        if(it.data.equals("Hủy gửi thành công")){
                            openDialogNoSubmission()
                        }
                        else {
                            submissionViewModel.getSubmission(
                                classviewmodel.classroom.value!!.classId, // re call submission
                                viewModel.homeWork.value!!.fileId
                            )
                        }
                        Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()

                    }

                    is DataState.Error -> {
                        dialog.btn_confirm_or_cancel.text =resources.getString(R.string.confirm)
                        dialog.process_submission.visibility= View.GONE
                        submissionViewModel.setEmptyStatus()
                        Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun obversePostHomeWorkStatus() {
        lifecycleScope.launchWhenCreated {
            viewModel.postHomeWorkStatus.collect {
                when (it) {
                    is DataState.Loading -> {
                        dialog.process.visibility=View.VISIBLE
                        dialog.btn_confirm.text=resources.getString(R.string.pls_wait)
                    }
                    is DataState.Success -> {
                        dialog.process.visibility=View.GONE
                        dialog.btn_confirm.text= resources.getString(R.string.confirm)
                        clearData()
                        viewModel.getData(classviewmodel.classroom.value!!.classId)
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                        viewModel.setEmptyHomeWorkStatus()
                    }
                    is DataState.Error -> {
                        dialog.process.visibility=View.GONE
                        dialog.btn_confirm.text= resources.getString(R.string.confirm)
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                        viewModel.setEmptyHomeWorkStatus()
                    }
                }
            }
        }
    }

    private fun openDiaLogHaveSubmission(submission: SubmissionX) {
        dialog = Dialog(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.submission_dialog, null)
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val index: Int = submission.name.lastIndexOf('.')
        val type: String = submission.name.substring(index + 1)
        dialog.noSubmission.visibility = View.GONE
        dialog.submission.visibility = View.VISIBLE
        dialog.name.text = submission.name
        dialog.by.text = submission.student.name
        if (submission.sizeInByte < 1024)
            dialog.size.text = "dung lượng: " + submission.sizeInByte.toString() + " Byte"
        else
            if (submission.sizeInByte > 1048576)
                dialog.size.text =
                    "dung lượng: " + (submission.sizeInByte / 1048576).toString() + " Mb"
            else
                dialog.size.text =
                    "dung lượng: " + (submission.sizeInByte / 1024).toString() + " Kb"
        dialog.time.text = "Nộp lúc: " + submission.dateCreated
        HomeWorkAdapter.setImageViewByType(type, view)
        if (submission.late) {
            dialog.status.text = "Trễ"
            dialog.status.setTextColor(resources.getColor(R.color.red))
        } else {
            dialog.status.text = "Đúng hạn"
            dialog.status.setTextColor(resources.getColor(R.color.green))
        }
        dialog.btn_confirm_or_cancel.text = resources.getString(R.string.cancel_submit)
        dialog.cancel_button.setOnClickListener {
            clearData()
            dialog.dismiss()
        }
        dialog.btn_confirm_or_cancel.setOnClickListener {

            submissionViewModel.deleteSubmission(classviewmodel.classroom.value!!.classId,
                submissionViewModel.submissionSelected.value!!.fileId)
        }

        dialog.show()
    }

    private fun openDialogNoSubmission() {
        dialog = Dialog(requireContext())
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.submission_dialog)
        dialog.submission.visibility = View.GONE
        dialog.noSubmission.visibility = View.VISIBLE
        dialog.btn_confirm_or_cancel.text = resources.getString(R.string.confirm)
        dialog.cancel_button.setOnClickListener {
            clearData()
            dialog.dismiss()
        }
        dialog.file.setOnClickListener {
            openFile()
        }
        dialog.btn_confirm_or_cancel.setOnClickListener {
        if(file==null || filename==null) {
            Toast.makeText(requireContext(),"Chưa chọn bài tập",Toast.LENGTH_SHORT).show()

        }else {
            submissionViewModel.postSubmission(
                classviewmodel.classroom.value!!.classId,
                viewModel.homeWork.value!!.fileId, filename!!, file!!
            )
        }
        }
        dialog.show()
    }

    private fun obverseSubmission() {
        lifecycleScope.launchWhenCreated {
            submissionViewModel.submission.collect {
                when (it) {

                    is DataState.Success -> {
                        if (it.data != null) {
                            submissionViewModel.setSubmissionSelected(it.data)
                            openDiaLogHaveSubmission(it.data)

                        } else {
                            openDialogNoSubmission()
                        }
                    }
                    is DataState.Error -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



}