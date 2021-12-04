package com.example.doancn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.ClassViewModel.ClassEvent.*
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.QrCodeX
import com.example.doancn.Models.classModel.ClassQuest
import com.example.doancn.Repository.AttendanceRepository
import com.example.doancn.Repository.ClassRepository
import com.example.doancn.Repository.EnrollmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ClassViewModel
@Inject
constructor(

    @Named("auth_token") val token: String?,
    @Named("user_role") val role: String?,
    private val classRepository: ClassRepository,
    private val attendanceRepository: AttendanceRepository,
    private val enrollmentRepository: EnrollmentRepository,
) : ViewModel() {

    sealed class ClassEvent<out R>() {
        data class Success<out T>(val data: T) : ClassEvent<T>()
        data class Error(val data: String) : ClassEvent<Nothing>()
        object Empty : ClassEvent<Nothing>()
        object Loading : ClassEvent<Nothing>()
    }

    private val _acceptState = MutableStateFlow<ClassEvent<Boolean>>(Empty)
    val acceptState: MutableStateFlow<ClassEvent<Boolean>> get() = _acceptState

    private val mutableSelectedItem = MutableLiveData<ClassQuest.Location>()
    val selectedItem: LiveData<ClassQuest.Location> get() = mutableSelectedItem
    private val _getQrCodeStatus = MutableStateFlow<DataState<QrCodeX?>>(DataState.Empty)
    val getQrCodeStatus: StateFlow<DataState<QrCodeX?>> = _getQrCodeStatus
    private val _attendanceStatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val attendanceStatus: StateFlow<DataState<String>> = _attendanceStatus
    fun selectItem(item: ClassQuest.Location) {
        mutableSelectedItem.value = item
    }


    private val _deleteState =
        MutableStateFlow<ClassEvent<String>>(Empty)
    val deleteState: StateFlow<ClassEvent<String>> = _deleteState


    private val _updateState =
        MutableStateFlow<ClassEvent<Classroom>>(Empty)
    val updateState: StateFlow<ClassEvent<Classroom>> = _updateState

    fun resetUpdateState() {
        _updateState.value = Empty
    }

    private val _classroom = MutableLiveData<Classroom>()
    val classroom: LiveData<Classroom> get() = _classroom

    fun selectItem(classroom: Classroom) {
        _classroom.value = classroom
    }

    fun createQR() {
        viewModelScope.launch {
            _getQrCodeStatus.value=DataState.Loading
            _getQrCodeStatus.value=attendanceRepository.getQrcodeToken(classroom.value!!.classId, token!!)
            _getQrCodeStatus.value=DataState.Empty
        }
    }


    fun doAttendance(qr: String) {
        viewModelScope.launch {
            _attendanceStatus.value=DataState.Loading
            _attendanceStatus.value=  attendanceRepository.doAttendance(classroom.value!!.classId,qr, token!!)
            _attendanceStatus.value=DataState.Empty
        }

    }


    fun deleteCurrentClass() {
        viewModelScope.launch(Dispatchers.IO) {
            _deleteState.value = Loading
            delay(1000)
            when (val state =
                classRepository.deleteClass(classroom.value!!.classId, token!!)) {
                is DataState.Success -> {
                    _deleteState.value =
                        Success(state.data)
                }
                is DataState.Error -> {
                    _deleteState.value = Error(state.data)
                }
            }
        }
    }

    fun updateClassroom(classRoom: ClassQuest) {
        viewModelScope.launch(Dispatchers.IO) {
            _updateState.value = Loading
            when (val state =
                classRepository.updateClass(classroom.value!!.classId, classRoom, token!!)) {
                is DataState.Success -> {
                    _updateState.value = Success(state.data)
                }
                is DataState.Error -> {
                    _updateState.value = Error(state.data)
                }
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = Empty
    }

    fun acceptEnrollment(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _acceptState.value = Loading
            when (val state = enrollmentRepository.acceptEnrollment(classroom.value!!.classId,userId,token!!)) {
                is DataState.Success -> {
                    _acceptState.value = Success(state.data)
                }
                is DataState.Error -> {
                    _acceptState.value = Error(state.data)
                }
            }
        }
    }

    fun resetAcceptState() {
        _acceptState.value = Empty
    }


}