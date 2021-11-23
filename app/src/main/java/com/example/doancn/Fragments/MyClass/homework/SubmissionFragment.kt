package com.example.doancn.Fragments.MyClass.homework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.doancn.Adapters.SubmissionAdapter
import com.example.doancn.ClassViewModel
import com.example.doancn.DI.DataState
import com.example.doancn.Models.HomeWorkX
import com.example.doancn.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.submission_fragment.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SubmissionFragment : Fragment() {

    private val viewModel: SubmissionViewModel by viewModels()
    private val classviewmodel: ClassViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.submission_fragment, container, false)



        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = arguments
        val homework = bundle?.getSerializable("targetHomework") as HomeWorkX
        obverseData()
        homework.let {
            viewModel.getData(classviewmodel.classroom.value!!.classId,it.fileId)
        }
        super.onViewCreated(view, savedInstanceState)
    }
    private  fun obverseData(){
        lifecycleScope.launchWhenCreated {
            viewModel.submissions.collect {
                when(it){
                    is DataState.Loading -> requireView().process.visibility=View.VISIBLE
                    is DataState.Success -> {
                        requireView().process.visibility=View.GONE
                        requireView().listview.adapter= SubmissionAdapter(requireContext(),
                            it.data!!
                        )

                    }
                    is DataState.Error -> {
                        requireView().process.visibility=View.GONE

                    }
                }
            }
        }
    }
}