package com.example.doancn.ViewModels

import androidx.lifecycle.ViewModel
import com.example.doancn.Repository.ExampleRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ExampleViewmodel(val repository: ExampleRepository) : ViewModel() {
    lateinit var name : String
    fun gido() {
        GlobalScope.launch {
           name= repository.example()
        }
    }
}