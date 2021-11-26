package com.example.doancn

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.ClassViewModel.ClassEvent.*
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.classModel.ClassQuest
import com.example.doancn.Repository.ClassRepository
import com.example.doancn.Utilities.QrCodeManager
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
    private var qrCodeManager: QrCodeManager,
    @Named("auth_token") val token: String?,
    @Named("user_role") val role: String?,
    private val classRepository: ClassRepository,
) : ViewModel() {

    sealed class ClassEvent<out R>() {
        data class Success<out T>(val data: T) : ClassEvent<T>()
        data class Error(val data: String) : ClassEvent<Nothing>()
        object Empty : ClassEvent<Nothing>()
        object Loading : ClassEvent<Nothing>()
    }

    private val mutableSelectedItem = MutableLiveData<ClassQuest.Location>()
    val selectedItem: LiveData<ClassQuest.Location> get() = mutableSelectedItem
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

    fun createQR(classId: Long, context: Context) {
        qrCodeManager.getQrCode(context, classId, token!!)
    }

    fun doAttendance(qr: String, context: Context) {
        qrCodeManager.doAttendace(classId = 2L, qr, token!!, context)
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
            _deleteState.value = Loading
            delay(1000)
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

}