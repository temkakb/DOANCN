package com.example.doancn.Fragments.MyClass.more

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doancn.Adapters.AttendancedStudentsAdapter
import com.example.doancn.Adapters.SectionRcAdapter
import com.example.doancn.ClassViewModel
import com.example.doancn.MainViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.SectionX
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import com.example.doancn.Repository.SectionsRepository
import com.example.doancn.Utilities.StringUtils
import com.example.doancn.databinding.FragmentSectionBinding
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.show_attendanced_students.*
import kotlinx.android.synthetic.main.show_attendanced_students.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class SectionFragment : Fragment(), SectionRcAdapter.OnSectionItemClickListener {

    private var _binding: FragmentSectionBinding? = null
    private  var sections : List<SectionX>? =null

    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val classViewModel: ClassViewModel by activityViewModels()
    private val sectionsRepository : SectionsRepository = SectionsRepository()
    private val linearLayout = LinearLayoutManager(activity)
    var token :String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        token = mainViewModel.token.toString()
        // Inflate the layout for this fragment
        _binding = FragmentSectionBinding.inflate(inflater, container, false)
        classViewModel.classroom.observe(viewLifecycleOwner, {
            setUpShift(it)
        })
        return binding.root
    }

    private fun setUpShift(classroom: Classroom) {
        val group = binding.toggleButtonGroup
        for (shift in classroom.shifts.sortedByDescending { it.dayOfWeek.dowId }.reversed()) {

            val button = layoutInflater.inflate(
                R.layout.single_button_layout,
                group,
                false
            ) as MaterialButton
            button.text = StringUtils.dowFormatter(shift.dayOfWeek.dowName)
            button.id = shift.shiftId
            group.addView(button);

        }
        group.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            GlobalScope.launch {
                withContext(Dispatchers.Main){
                    _binding!!.process.visibility=View.VISIBLE
                }
                try {
                    sections =sectionsRepository.getSectionsOfShift(checkedId.toLong())
                    withContext(Dispatchers.Main){
                        _binding!!.process.visibility=View.GONE
                        _binding!!.sectionRcView.layoutManager=linearLayout
                        _binding!!.sectionRcView.adapter= SectionRcAdapter(sections!!,this@SectionFragment)
                    }

                }catch (e: HttpException){
                    withContext(Dispatchers.Main){
                        _binding!!.process.visibility=View.GONE
                        Toast.makeText(requireContext(),e.message(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            // Respond to button selection
            Log.d("checked", checkedId.toString())

        }


    }

    //On click to see list attendance student
    override fun onDetailItemClick(position: Int) {
        val section = sections!![position]
        var list : List<UserMe>? = null
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                _binding!!.process.visibility=View.VISIBLE
            }
            try {
                list = sectionsRepository.getAttendanceStudents(section.sectionId.toLong(), token!!)
                Log.i("So luong sv", list!!.size.toString())
                withContext(Dispatchers.Main){
                    _binding!!.process.visibility=View.GONE
                    val showAttendanceStudentLayout: View = LayoutInflater.from(context)
                        .inflate(R.layout.show_attendanced_students, null)
                    val attendancedStudentsAdapter = AttendancedStudentsAdapter(
                        showAttendanceStudentLayout.context,
                        list = list!!
                    )
                    showAttendanceStudentLayout.list_attendanced_students.adapter = attendancedStudentsAdapter
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setView(showAttendanceStudentLayout)
                    val alertDialog = builder.create()
                    alertDialog!!.show()
                    showAttendanceStudentLayout.cancel_attendance_button.setOnClickListener {
                        alertDialog.dismiss()
                    }
                }

            }catch (e: HttpException){
                withContext(Dispatchers.Main){
                    _binding!!.process.visibility=View.GONE
                    Toast.makeText(requireContext(),e.message(),Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}
