package com.example.doancn.Fragments.MyClass

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Adapters.ListClassAdapter
import com.example.doancn.MainViewModel
import com.example.doancn.MainViewModel_Factory
import com.example.doancn.Models.Classroom
import com.example.doancn.R
import com.example.doancn.databinding.MyClassRoomFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MyClassRoomFragment : Fragment() {

    companion object {
        fun newInstance() = MyClassRoomFragment()
    }

    private var _binding: MyClassRoomFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListClassAdapter

    private var listItems: ArrayList<Classroom> = ArrayList()

    private val viewModel: MyClassRoomViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MyClassRoomFragmentBinding.inflate(inflater, container, false)
        recyclerView = binding.myClass
        initRecyclerView()
        observeData()
        if(mainViewModel.role == "TEACHER"){
            binding.navigateBtn.text = "Tạo lớp học"
            binding.navigateBtn.setOnClickListener{
                findNavController().navigate(R.id.action_nav_myClass_to_nav_createclass)
            }
        }else{
            binding.navigateBtn.text = "Tham gia lớp học"
            binding.navigateBtn.setOnClickListener{
                findNavController().navigate(R.id.action_nav_myClass_to_nav_joinclass)
            }

        }
        return binding.root
    }

    override fun onResume() {
        viewModel.getClassList()
        super.onResume()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.classList.collect { event ->
                when (event) {
                    is MyClassRoomViewModel.GetClassEvent.Success -> {
                        listItems.clear();
                        listItems.addAll(event.data)
                        if (listItems.isEmpty()) {
                            binding.myClass.visibility = View.GONE
                            binding.announcement.visibility = View.VISIBLE
                            binding.navigateBtn.visibility = View.VISIBLE
                            binding.loadClassProgressBar.visibility = View.GONE
                        } else{
                            binding.myClass.visibility = View.VISIBLE
                            binding.announcement.visibility = View.GONE
                            binding.navigateBtn.visibility = View.GONE
                            binding.loadClassProgressBar.visibility = View.GONE
                        }

                        Log.d("observeData", listItems.toString())
                        adapter.notifyDataSetChanged()


                    }
                    is MyClassRoomViewModel.GetClassEvent.Empty -> {
                        listItems.clear();
                        binding.announcement.visibility = View.GONE
                        binding.navigateBtn.visibility = View.GONE
                    }
                    is MyClassRoomViewModel.GetClassEvent.Error -> {
                        binding.announcement.visibility = View.GONE
                        binding.navigateBtn.visibility = View.GONE

                    }
                    is MyClassRoomViewModel.GetClassEvent.Loading -> {
                        binding.announcement.visibility = View.GONE
                        binding.navigateBtn.visibility = View.GONE
                        binding.loadClassProgressBar.visibility = View.VISIBLE
                        binding.myClass.visibility = View.INVISIBLE
                    }
                }

            }
        }

    }

    private fun initRecyclerView() {
        val linearLayout = LinearLayoutManager(activity)
        adapter = ListClassAdapter(
            requireActivity(),
            listItems
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayout

    }


}