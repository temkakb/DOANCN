package com.example.doancn.Fragments.MyClass.homework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.doancn.ClassViewModel
import com.example.doancn.DI.DataState
import com.example.doancn.Models.HomeWorkX
import com.example.doancn.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_work_bottom_sheet_fragment.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class BottomSheetHomeWorkFragment : BottomSheetDialogFragment() {
    private val viewModel: HomeworkViewModel by viewModels()
    private val classviewmodel: ClassViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.home_work_bottom_sheet_fragment,container,false)
        val bundle = arguments
        val homework = bundle?.getSerializable("targetHomework") as HomeWorkX
        obverseData()
        view.delete.setOnClickListener {
            homework.let {
                viewModel.deleteHomeWork(classviewmodel.classroom.value!!.classId,homework.fileId)
            }
        }
        return  view
    }
    private fun obverseData(){
        lifecycleScope.launchWhenCreated {
            viewModel.deleteHomeWorkStatus.collect {
                when(it){
                    is DataState.Loading->{
                        requireView().process.visibility=View.VISIBLE
                    }
                    is DataState.Success-> {
                        viewModel.getData(classviewmodel.classroom.value!!.classId)
                        dismiss()
                        Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()

                    }
                    is DataState.Error->
                    {
                        dismiss()
                        Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}