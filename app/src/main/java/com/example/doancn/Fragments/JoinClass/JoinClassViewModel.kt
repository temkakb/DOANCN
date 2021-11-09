package com.example.doancn.Fragments.JoinClass

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

    private val _classrooms = MutableStateFlow<DataState<List<Classroom>?>>(DataState.Empty)
    val classrooms: StateFlow<DataState<List<Classroom>?>> = _classrooms

    fun getClassRoomToEnroll(city: String, subjectId: Long?) {
        viewModelScope.launch {
            _classrooms.value = DataState.Loading
            _classrooms.value = enrollmentRepository.getclassenrollment(city, subjectId, token!!)

        }
    }
}