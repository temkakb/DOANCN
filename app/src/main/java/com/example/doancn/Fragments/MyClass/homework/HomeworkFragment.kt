package com.example.doancn.Fragments.MyClass.homework

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.doancn.R

class HomeworkFragment : Fragment() {

    companion object {
        fun newInstance() = HomeworkFragment()
    }

    private lateinit var viewModel: HomeworkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.homework_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeworkViewModel::class.java)
        // TODO: Use the ViewModel
    }

}