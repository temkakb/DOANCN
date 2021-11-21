package com.example.doancn.Fragments.MyClass.people

import android.annotation.SuppressLint
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
import com.example.doancn.Adapters.StudentInClassAdapter
import com.example.doancn.Adapters.StudentParentAdapter
import com.example.doancn.ClassViewModel
import com.example.doancn.DI.DataState
import com.example.doancn.MainViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import com.example.doancn.databinding.PeopleFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.detail_classroom_dialog.*
import kotlinx.android.synthetic.main.edit_parent.view.*
import kotlinx.android.synthetic.main.fragment_joinclass.view.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.people_fragment.*
import kotlinx.android.synthetic.main.people_fragment.view.*
import kotlinx.android.synthetic.main.show_payment_history.view.*
import kotlinx.android.synthetic.main.show_student_info.view.*
import kotlinx.android.synthetic.main.weekcalendar_week.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
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

        peopleViewModel.paystatus.observe(viewLifecycleOwner, {
            when (it) {
                is PeopleViewModel.PayEvent.Success -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Thành công")
                        .setMessage(it.data)
                        .setNegativeButton("OK") { _, _ -> }
                        .show()
                    binding.rcvStudentInClass.visibility = View.VISIBLE
                    binding.PayProgressBar.visibility = View.INVISIBLE
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                is PeopleViewModel.PayEvent.Error -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Lỗi")
                        .setMessage(it.data)
                        .setPositiveButton("Xác nhận") { _, _ -> }
                        .show()
                    binding.rcvStudentInClass.visibility = View.VISIBLE
                    binding.PayProgressBar.visibility = View.INVISIBLE
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                is PeopleViewModel.PayEvent.Loading -> {
                    binding.rcvStudentInClass.visibility = View.INVISIBLE
                    binding.PayProgressBar.visibility = View.VISIBLE
                    requireActivity().window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
            }

        })

        classroom = classViewModel.classroom.value!!
        Log.i("Token",mainViewModel.token.toString().trim())
        getListStudent(mainViewModel.token.toString())
        Log.i("ROLE",mainViewModel.role.toString())

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
            if(mainViewModel.role.toString() == "STUDENT" || classroom!!.option.paymentOptionId == 6L)
                constraintLayout5.visibility = View.GONE

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
                                    ,peopleViewModel, mainViewModel.token.toString()
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
            if (student.image != null) {
                val imgDecode: ByteArray = Base64.getDecoder().decode(student.image)
                val bmp = BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
                showStudentInfoLayout.student_info_img.setImageBitmap(bmp)
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
            showStudentInfoLayout.student_info_name.text = student.name
            showStudentInfoLayout.student_info_email.text = student.account.email
            showStudentInfoLayout.student_info_dob.text = student.dob
            showStudentInfoLayout.student_info_education_level.text = student.educationLevel
            showStudentInfoLayout.student_info_curent_work_place.text = student.currentWorkPlace
            showStudentInfoLayout.student_info_phone.text = student.phoneNumber
            showStudentInfoLayout.student_info_adress.text = student.address
            when (student.gender.genderID) {
                1 -> showStudentInfoLayout.student_info_gender.text = R.string.Male.toString()
                2 -> showStudentInfoLayout.student_info_gender.text = R.string.Female.toString()
                else -> showStudentInfoLayout.student_info_gender.text = R.string.Orther.toString()
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
            showStudentInfoLayout.cancel_button.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    override fun showPaymentHistory(position: Int) {
        /*if(mainViewModel.role.toString() == "TEACHER"){
            val student: UserMe = listStudent[position]
            val showPaymnetInfoLayout: View = LayoutInflater.from(context)
                .inflate(R.layout.show_payment_history, null)

            showPaymnetInfoLayout.

            val builder = AlertDialog.Builder(requireContext())
            builder.setView(showPaymnetInfoLayout)
            val dialog = builder.create()
            dialog.show()
            showPaymnetInfoLayout.cancel_payment_button.setOnClickListener {
                dialog.dismiss()
            }
        }*/
    }

}