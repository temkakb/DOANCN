package com.example.doancn.Fragments.MyClass.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.doancn.ClassViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.R
import com.example.doancn.databinding.FragmentSectionBinding
import com.google.android.material.button.MaterialButton


class SectionFragment : Fragment() {

    private var _binding: FragmentSectionBinding? = null

    private val binding get() = _binding!!
    private val classViewModel: ClassViewModel by activityViewModels()

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
        for (shift in classroom.shifts) {

            val button = layoutInflater.inflate(
                R.layout.single_button_layout,
                group,
                false
            ) as MaterialButton
            button.text = shift.dayOfWeek.dowName
            group.addView(button);

        }
        group.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            // Respond to button selection
        }


    }
}