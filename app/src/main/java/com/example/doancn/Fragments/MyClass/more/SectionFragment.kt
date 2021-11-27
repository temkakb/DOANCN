package com.example.doancn.Fragments.MyClass.more

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doancn.Adapters.SectionRcAdapter
import com.example.doancn.ClassViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.SectionX
import com.example.doancn.R
import com.example.doancn.Repository.SectionsRepository
import com.example.doancn.Utilities.StringUtils
import com.example.doancn.databinding.FragmentSectionBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class SectionFragment : Fragment() {

    private var _binding: FragmentSectionBinding? = null
    private  var sections : List<SectionX>? =null

    private val binding get() = _binding!!
    private val classViewModel: ClassViewModel by activityViewModels()
    private val sectionsRepository : SectionsRepository = SectionsRepository()
    private val linearLayout = LinearLayoutManager(activity)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSectionBinding.inflate(inflater, container, false)
        classViewModel.classroom.observe(viewLifecycleOwner, {
            setUpShift(it)
        })
        return binding.root
    }

    private fun setUpShift(classroom: Classroom) {
        val group = binding.toggleButtonGroup
        for (shift in classroom.shifts.reversed()) {

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
                        _binding!!.sectionRcView.adapter= SectionRcAdapter(sections!!)
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
}
