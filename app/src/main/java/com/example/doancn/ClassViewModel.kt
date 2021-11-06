package com.example.doancn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doancn.Models.Classroom

class ClassViewModel : ViewModel() {
    private val _classroom = MutableLiveData<Classroom>()
    val classroom: LiveData<Classroom> get() = _classroom

    fun selectItem(classroom: Classroom) {
        _classroom.value = classroom
    }

}