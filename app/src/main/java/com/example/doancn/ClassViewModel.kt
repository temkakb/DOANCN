package com.example.doancn

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.Utilities.QrCodeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ClassViewModel
@Inject
constructor(
    private var qrCodeManager: QrCodeManager,
    @Named("auth_token") val token: String?
) : ViewModel() {
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
}