package com.example.doancn.Fragments.JoinClass

import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import com.example.doancn.Repository.EnrollmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltViewModel
class JoinClassViewModel
@Inject constructor(
    val enrollmentRepository: EnrollmentRepository,
    @Named("auth_token") private val token: String?
) : ViewModel() {

    var view: View? = null
    var btnview: Button? = null
    private val _classrooms = MutableStateFlow<DataState<List<Classroom>?>>(DataState.Empty)
    val classrooms: StateFlow<DataState<List<Classroom>?>> = _classrooms
    private val _enrollstatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val enrollstatus: StateFlow<DataState<String>> = _enrollstatus


    fun getClassRoomToEnroll(city: String, subjectId: Long?) {

        viewModelScope.launch {
            _classrooms.value = DataState.Loading
            _classrooms.value = enrollmentRepository.getclassenrollment(city, subjectId, token!!)
        }
    }

    fun doEnroll(classroom: Classroom) {
        viewModelScope.launch {
            _enrollstatus.value = DataState.Loading
            _enrollstatus.value = enrollmentRepository.doEnroll(classroom.classId, token!!)
            when (_enrollstatus.value) {
                is DataState.Error -> classroom.enrolled = false
                is DataState.Success -> classroom.enrolled = true
            }
        }

    }

    fun doDeleteEnrollment(classroom: Classroom) {
        viewModelScope.launch {
            _enrollstatus.value = DataState.Loading
            _enrollstatus.value =
                enrollmentRepository.doDeleteEnrollment(classroom.classId, token!!)
            when (_enrollstatus.value) {
                is DataState.Error -> classroom.enrolled = true
                is DataState.Success -> classroom.enrolled = false
            }
        }
    }

    fun doSearch(keyword: String) {
        viewModelScope.launch {
            _classrooms.value = DataState.Loading
            _classrooms.value = enrollmentRepository.dosSearch(keyword, token!!)
        }
    }
}