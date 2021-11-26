package com.example.doancn.Fragments.MyClass.people

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Adapters.AttendancedStudentsAdapter
import com.example.doancn.Adapters.PayementHistoryAdapter
import com.example.doancn.Adapters.StudentInClassAdapter
import com.example.doancn.Adapters.StudentParentAdapter
import com.example.doancn.ClassViewModel
import com.example.doancn.DI.DataState
import com.example.doancn.MainViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import com.example.doancn.Repository.SectionsRepository
import com.example.doancn.databinding.PeopleFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.detail_classroom_dialog.*
import kotlinx.android.synthetic.main.edit_parent.view.*
import kotlinx.android.synthetic.main.fragment_joinclass.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.people_fragment.*
import kotlinx.android.synthetic.main.people_fragment.view.*
import kotlinx.android.synthetic.main.show_attendanced_students.*
import kotlinx.android.synthetic.main.show_attendanced_students.view.*
import kotlinx.android.synthetic.main.show_payment_history.view.*
import kotlinx.android.synthetic.main.show_student_info.view.*
import kotlinx.android.synthetic.main.weekcalendar_week.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PeopleFragment : Fragment() , StudentInClassAdapter.OnItClickListener {
    private val mainViewModel : MainViewModel by activityViewModels()
    private val classViewModel: ClassViewModel by activityViewModels()
    private var listStudent : ArrayList<UserMe> = ArrayList()
    private var studentInClassAdapter: StudentInClassAdapter? = null
    private var classroom: Classroom? = null
    private val peopleViewModel: PeopleViewModel by viewModels()
    private var _binding: PeopleFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = PeopleFragment()
    }

    private lateinit var viewModel: PeopleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = PeopleFragmentBinding.inflate(inflater, container, false)

        classroom = classViewModel.classroom.value!!
        getListStudent(mainViewModel.token.toString())

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(PeopleViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(classroom == null)
        {
            Toast.makeText(context,"Null class bà rồi",Toast.LENGTH_SHORT).show()
        }else{
            observeData()
        }

    }



    private fun getListStudent(token : String) {

        peopleViewModel.getUserOfClass(token,classroom!!.classId)
        lifecycleScope.launchWhenCreated {
            peopleViewModel.users.collect{
                if(it is DataState.Success){
                    listStudent = (it.data as ArrayList<UserMe>?)!!
                }
            }
        }
    }


    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            peopleViewModel.users.collect {
                when (it) {
                    is DataState.Loading -> {
                        requireView().process_student.visibility = View.VISIBLE
                    }
                    is DataState.Success -> {
                        requireView().process_student.visibility = View.GONE
                        if (it.data.isNullOrEmpty() || it.data.count() == 0) {
                            requireView().noStudent.visibility = View.VISIBLE
                            requireView().rcv_student_in_class.adapter = null
                            studentInClassAdapter = null
                        } else {
                            noStudent.visibility = View.INVISIBLE
                            if (studentInClassAdapter == null) {
                                requireView().rcv_student_in_class.layoutManager =
                                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
                                studentInClassAdapter = StudentInClassAdapter(classroom!!
                                    ,mainViewModel.role.toString()
                                    ,this@PeopleFragment
                                    , requireContext())
                                studentInClassAdapter!!.setData(listStudent)
                                Log.i("Số lượng học sinh",listStudent.count().toString())
                                requireView().rcv_student_in_class.adapter = studentInClassAdapter
                            } else studentInClassAdapter!!.setData(listStudent)
                        }
                    }
                    is DataState.Error -> {
                        requireView().process_student.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onItemClick(position: Int) {
        if(mainViewModel.role.toString() == "TEACHER"){

            val student: UserMe = listStudent[position]
            val showStudentInfoLayout: View = LayoutInflater.from(context)
                .inflate(R.layout.show_student_info, null)

            peopleViewModel.paystatus.observe(viewLifecycleOwner, {
                when (it) {
                    is PeopleViewModel.PayEvent.Success -> {
                        Toast.makeText(context,"Đóng học phí thành công",Toast.LENGTH_SHORT).show()
                        showStudentInfoLayout.student_info.visibility = View.VISIBLE
                        showStudentInfoLayout.Pay_progressBar.visibility = View.INVISIBLE
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                    is PeopleViewModel.PayEvent.Error -> {
                        Toast.makeText(context,"Đóng học phí thất bại",Toast.LENGTH_SHORT).show()
                        showStudentInfoLayout.student_info.visibility = View.VISIBLE
                        showStudentInfoLayout.Pay_progressBar.visibility = View.INVISIBLE
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                    is PeopleViewModel.PayEvent.Loading -> {
                        showStudentInfoLayout.student_info.visibility = View.INVISIBLE
                        showStudentInfoLayout.Pay_progressBar.visibility = View.VISIBLE
                        requireActivity().window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                    }
                }

            })

            if (student.image != null) {
                val imgDecode: ByteArray = Base64.getDecoder().decode(student.image)
                val bmp = BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
                val newbitMap = Bitmap.createScaledBitmap(bmp, 360, 360, true)
                showStudentInfoLayout.student_info_img.setImageBitmap(newbitMap)
            } else {
                when (student.gender.genderID) {
                    1 -> {
                        showStudentInfoLayout.student_info_img.setImageResource(R.drawable.man)
                    }
                    2 -> {
                        showStudentInfoLayout.student_info_img.setImageResource(R.drawable.femal)
                    }
                    3 -> {
                        showStudentInfoLayout.student_info_img.setImageResource(R.drawable.orther)
                    }
                }
            }
            for (i in student.enrollments!!) {
                if (i.classroom.classId == classroom!!.classId) {
                    showStudentInfoLayout.student_info_next_payment.text = i.nextPaymentAt
                }
            }
            showStudentInfoLayout.student_info_name.text = student.name
            showStudentInfoLayout.student_info_email.text = student.account.email
            showStudentInfoLayout.student_info_dob.text = student.dob
            if(student.phoneNumber != null)
                showStudentInfoLayout.student_info_education_level.text = student.educationLevel
            if(student.phoneNumber != null)
                showStudentInfoLayout.student_info_curent_work_place.text = student.currentWorkPlace
            if(student.phoneNumber != null)
                showStudentInfoLayout.student_info_phone.text = student.phoneNumber
            if(student.phoneNumber != null)
                showStudentInfoLayout.student_info_adress.text = student.address
            when (student.gender.genderID) {
                1 -> showStudentInfoLayout.student_info_gender.text = getString(R.string.Male)
                2 -> showStudentInfoLayout.student_info_gender.text = getString(R.string.Female)
                else -> showStudentInfoLayout.student_info_gender.text = getString(R.string.Orther)
            }
            if (student.parents.count() != 0) {
                showStudentInfoLayout.no_student_info_parent.visibility = View.GONE
                val studentParentAdapter =
                    StudentParentAdapter(showStudentInfoLayout.context, student.parents)
                showStudentInfoLayout.list_student_info_parent.adapter = studentParentAdapter
                showStudentInfoLayout.list_student_info_parent.setOnTouchListener { v, event ->
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    false
                }
            }
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(showStudentInfoLayout)
            val dialog = builder.create()
            dialog.show()

            showStudentInfoLayout.student_info_payment_history.setOnClickListener {
                var numberPayment  = 0
                for (i in student.enrollments!!) {
                    if (i.classroom.classId == classroom!!.classId) {
                        numberPayment = i.paymentHistories.count()
                    }
                }
                if(numberPayment != 0) {
                    if(mainViewModel.role == "TEACHER"){
                        val showPaymnetInfoLayout: View = LayoutInflater.from(context)
                            .inflate(R.layout.show_payment_history, null)
                        for (i in student.enrollments!!){
                            if(i.classroom.classId == classroom!!.classId){
                                val paymentHistoryAdapter =
                                    PayementHistoryAdapter(showPaymnetInfoLayout.context,i.paymentHistories)
                                showPaymnetInfoLayout.list_payment_history.adapter = paymentHistoryAdapter
                            }
                        }
                        val builderPaymentHistory = AlertDialog.Builder(requireContext())
                        builderPaymentHistory.setView(showPaymnetInfoLayout)
                        val dialogPayementHistory = builderPaymentHistory.create()
                        dialogPayementHistory.show()
                        showPaymnetInfoLayout.cancel_payment_button.setOnClickListener {
                            dialogPayementHistory.dismiss()
                        }
                    }
                } else
                    Toast.makeText(context,"Chưa có lịch sử đóng tiền",Toast.LENGTH_SHORT).show()
            }

            showStudentInfoLayout.cancel_button.setOnClickListener {
                dialog.dismiss()
            }

            showStudentInfoLayout.student_info_pay_fee.setOnClickListener {
                val formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                for(i in student.enrollments!!){
                    if(i.classroom.classId == classroom!!.classId){
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val nextPay = LocalDate.parse(i.nextPaymentAt, formatter)
                        when(classroom!!.option.paymentOptionId){
                            1L -> {
                                showStudentInfoLayout.student_info_next_payment.text = nextPay.plusWeeks(1L).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusWeeks(1L).format(formatters).toString()
                            }

                            2L -> {
                                showStudentInfoLayout.student_info_next_payment.text = nextPay.plusMonths(1L).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusMonths(1L).format(formatters).toString()
                            }

                            3L -> {
                                showStudentInfoLayout.student_info_next_payment.text = nextPay.plusMonths(3L).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusMonths(3L).format(formatters).toString()
                            }

                            4L -> {
                                showStudentInfoLayout.student_info_next_payment.text = nextPay.plusYears(1L).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusYears(1L).format(formatters).toString()
                            }

                            5L -> {
                                showStudentInfoLayout.student_info_next_payment.text = nextPay.plusYears(20000).format(formatters).toString()
                                i.nextPaymentAt = nextPay.plusYears(20000).format(formatters).toString()
                            }
                        }
                        peopleViewModel.updateStudentPayment(mainViewModel.token.toString(),id = i.enrollmentId,classroom!!.classId)
                    }
                }
            }
        }
    }

    private suspend fun showAttendanceStudent(token: String, sectionId : Long){
        val sectionRepo = SectionsRepository()
        val list : List<UserMe> = sectionRepo.getAttendanceStudents(token = token, sectionId = sectionId)
        val viewdialog =
            LayoutInflater.from(context).inflate(R.layout.detail_classroom_dialog, null)
        val dialog = Dialog(requireContext())
        val attendancedStudentsAdapter = AttendancedStudentsAdapter(requireContext(),list)
        viewdialog.list_attendanced_students.adapter = attendancedStudentsAdapter
        dialog.cancel_attendance_button.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}