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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Adapters.ListClassAdapter
import com.example.doancn.Models.Classroom
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MyClassRoomFragmentBinding.inflate(inflater, container, false)
        recyclerView = binding.myClass
        Log.d("MyClassRoomFragment", recyclerView.toString())
        initRecyclerView()
        viewModel.getClassList()
        observeData()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            viewModel.classList.collect { event ->
                when (event) {
                    is MyClassRoomViewModel.GetClassEvent.Success -> {
                        // Toast.makeText(requireContext(), "data received", Toast.LENGTH_SHORT).show()
                        binding.loadClassProgressBar.visibility = View.INVISIBLE
                        listItems.clear();
                        listItems.addAll(event.data);
                        Log.d("observeData", listItems.toString())
                        adapter.notifyDataSetChanged()

                    }
                    is MyClassRoomViewModel.GetClassEvent.Empty -> {
                        listItems.clear();
                        Toast.makeText(requireContext(), "empty data", Toast.LENGTH_SHORT).show()
                    }
                    is MyClassRoomViewModel.GetClassEvent.Error -> {
                        Toast.makeText(requireContext(), "error: ${event.data}", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("observeData", event.data.toString())

                    }
                    is MyClassRoomViewModel.GetClassEvent.Loading -> {
                        binding.loadClassProgressBar.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "data loading", Toast.LENGTH_SHORT).show()
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