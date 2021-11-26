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
import com.example.doancn.databinding.ClassHomeFragmentBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ClassHomeFragment : Fragment() {

    private var _binding: ClassHomeFragmentBinding? = null
    private val classViewModel: ClassViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var vp2: ViewPager2

    companion object {
        fun newInstance() = ClassHomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ClassHomeFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupAnnouncementTable()
        if (arguments?.containsKey("classRoomHome") == true) {
            val classroom =
                arguments?.getSerializable("classRoomHome") as Classroom
            Log.d("classroom", classroom.toString())
            classViewModel.selectItem(classroom)
        }

        return root
    }

    private fun setupAnnouncementTable() {
        vp2 = binding.vp2Announcement
        vp2.adapter = AnnouncementAdapter(requireContext(), Announcement.listOfAnnouncement())
        vp2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }


}
