package com.example.doancn.Fragments.MyClass.people

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Adapters.ListStudentAdapter
import com.example.doancn.ClassViewModel
import com.example.doancn.Fragments.MyClass.people.PeopleViewModel.GetListStudentEvent.*
import com.example.doancn.Models.User
import com.example.doancn.databinding.PeopleFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PeopleFragment : Fragment() {

    companion object {
        fun newInstance() = PeopleFragment()
    }


    private var _binding: PeopleFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListStudentAdapter
    private var listItems: ArrayList<User> = ArrayList()


    private val viewModel: PeopleViewModel by viewModels()
    private val classViewModel: ClassViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PeopleFragmentBinding.inflate(inflater, container, false)

        recyclerView = binding.studentList
        initRecyclerView()
        viewModel.getStudentList(classViewModel.classroom.value!!.classId)
        observeData()
        return binding.root
    }

    private fun initRecyclerView() {
        val linearLayout = LinearLayoutManager(activity)
        adapter = ListStudentAdapter(
            requireActivity(),
            listItems
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayout
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            viewModel.studentList.collect { event ->
                when (event) {
                    is Success -> {
                        listItems.clear();
                        listItems.addAll(event.data);
                        Log.d("observeData", listItems.toString())
                        adapter.notifyDataSetChanged()

                    }
                    is Empty -> {
                        listItems.clear();
                        Toast.makeText(requireContext(), "empty data", Toast.LENGTH_SHORT).show()
                    }
                    is Error -> {
                        Toast.makeText(requireContext(), "error: ${event.data}", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("observeData", event.data.toString())

                    }
                    is Loading -> {
                        Toast.makeText(requireContext(), "data loading", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

    }

}