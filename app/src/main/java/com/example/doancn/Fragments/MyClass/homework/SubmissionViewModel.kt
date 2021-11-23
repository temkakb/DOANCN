package com.example.doancn.Fragments.MyClass.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.SubmissionX
import com.example.doancn.Repository.ClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
@ExperimentalCoroutinesApi
class SubmissionViewModel
@Inject constructor(
    private val classRepository: ClassRepository,
    @Named("auth_token") private val token: String?,

    )
    : ViewModel() {

    private val _submissions= MutableStateFlow<DataState<List<SubmissionX>?>>(DataState.Empty)
    val submissions: StateFlow<DataState<List<SubmissionX>?>> = _submissions
    private val _submission= MutableStateFlow<DataState<SubmissionX?>>(DataState.Empty)
    val submission: StateFlow<DataState<SubmissionX?>> = _submission

    fun getData(id: Long,homeworkId : Long){
        viewModelScope.launch {
            _submissions.value = DataState.Loading
            _submissions.value= classRepository.getSubmissions(id,homeworkId, token!!)
            _submissions.value=DataState.Empty
        }
    }
    fun getSubmission(id: Long,homeworkId: Long){
        viewModelScope.launch {
            _submission.value=DataState.Loading
            _submission.value=classRepository.getSubmission(id,homeworkId,token!!)
            _submission.value= DataState.Empty
        }
    }

}