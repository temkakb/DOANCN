package com.example.doancn.Fragments.MyClass.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.doancn.Adapters.AnnouncementAdapter
import com.example.doancn.ClassViewModel
import com.example.doancn.Models.Announcement
import com.example.doancn.Models.Classroom
import com.example.doancn.R
import com.example.doancn.databinding.ClassHomeFragmentBinding
import com.google.android.material.button.MaterialButton

class ClassHomeFragment : Fragment() {

    private var _binding: ClassHomeFragmentBinding? = null
    private val classViewModel: ClassViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var vp2: ViewPager2

    companion object {
        fun newInstance() = ClassHomeFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ClassHomeFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupAnnouncementTable()
        classViewModel.classroom.observe(viewLifecycleOwner, {
            setUpShift(it)
        })
        return root
    }


    private fun setupAnnouncementTable() {
        vp2 = binding.vp2Announcement
        vp2.adapter = AnnouncementAdapter(requireContext(), Announcement.listOfAnnouncement())
        vp2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }


    private fun setUpShift(classroom: Classroom) {
        val group = binding.toggleButtonGroup
        Log.d("ClassHomeFragment", "classroom: $classroom")
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