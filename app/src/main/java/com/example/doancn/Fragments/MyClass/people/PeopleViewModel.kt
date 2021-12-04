package com.example.doancn.Fragments.MyClass.people

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.ClassViewModel
import com.example.doancn.DI.DataState
import com.example.doancn.Models.UserMe
import com.example.doancn.Repository.ClassRepository
import com.example.doancn.Repository.EnrollmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltViewModel
class PeopleViewModel
@Inject constructor(
    private val classRepository: ClassRepository,
    private val enrollmentRepository: EnrollmentRepository,
    @Named("auth_token")
    private val token: String?
) : ViewModel() {
    private val _enrolments = MutableStateFlow<GetEnrollmentEvent<ArrayList<UserMe>?>>(GetEnrollmentEvent.Empty(ArrayList()))
    val enrolments: StateFlow<GetEnrollmentEvent<ArrayList<UserMe>?>> = _enrolments


    private val _users = MutableStateFlow<DataState<List<UserMe>?>>(DataState.Empty)
    val users: StateFlow<DataState<List<UserMe>?>> = _users

    private val _paystatus: MutableLiveData<PayEvent<String>> = MutableLiveData()
    val paystatus: LiveData<PayEvent<String>>
        get() = _paystatus

    fun getUserOfClass(token: String, id: Long) {
        viewModelScope.launch {
            _users.value = DataState.Loading
            _users.value = classRepository.getUserOfClass(token, id)
        }
    }

    fun updateStudentPayment(token: String, id: Int, classId: Long) {
        viewModelScope.launch {
            val s: Flow<DataState<String>> = classRepository.updateStudentPayment(token, id)
            s.onEach {
                when (it) {
                    is DataState.Success -> {
                        _users.value = classRepository.getUserOfClass(token, classId)
                        _paystatus.value = PayEvent.Success(it.data)
                    }
                    is DataState.Error -> {
                        _paystatus.value = PayEvent.Error(it.data)
                    }
                    is DataState.Loading -> {
                        _paystatus.value = PayEvent.Loading
                    }
                    else -> Log.d("TAG", "No data")
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getEnrollments(classId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _enrolments.value = GetEnrollmentEvent.Loading
            when (val state = enrollmentRepository.getEnrollment(classId,token!!)) {
                is DataState.Success -> {
                    _enrolments.value = GetEnrollmentEvent.Success(state.data)
                }
                is DataState.Error -> {
                    _enrolments.value = GetEnrollmentEvent.Error(state.data)
                }
            }
        }
    }

    fun resetEnrollmentState() {
        _enrolments.value = GetEnrollmentEvent.Empty(ArrayList())
    }

    sealed class GetListStudentEvent<out R>() {
        data class Success<out T>(val data: T) : GetListStudentEvent<T>()
        data class Error(val data: String) : GetListStudentEvent<Nothing>()
        data class Empty<out T>(val data: T) : GetListStudentEvent<T>()
        object Loading : GetListStudentEvent<Nothing>()


    }


    sealed class GetEnrollmentEvent<out R>() {
        data class Success<out T>(val data: T) : GetEnrollmentEvent<T>()
        data class Error(val data: String) : GetEnrollmentEvent<Nothing>()
        data class Empty<out T>(val data: T) : GetEnrollmentEvent<T>()
        object Loading : GetEnrollmentEvent<Nothing>()


    }

    sealed class PayEvent<out R>() {
        data class Success<out T>(val data: T) : PayEvent<T>()
        data class Error(val data: String) : PayEvent<Nothing>()
        object Loading : PayEvent<Nothing>()
    }

}