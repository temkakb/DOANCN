package com.example.doancn.Fragments.MyClass.people

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.PaymentHistory
import com.example.doancn.Models.UserMe
import com.example.doancn.Repository.ClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PeopleViewModel
@Inject constructor(
    private val classRepository: ClassRepository
) : ViewModel() {
    private val _users = MutableStateFlow<DataState<List<UserMe>?>>(DataState.Empty)
    val users: StateFlow<DataState<List<UserMe>?>> = _users

    private val _userPaymentHistories = MutableStateFlow<DataState<List<PaymentHistory>?>>(DataState.Empty)
    val userPaymentHistories: StateFlow<DataState<List<PaymentHistory>?>> = _userPaymentHistories

    private val _paystatus: MutableLiveData<PayEvent<String>> = MutableLiveData()
    val paystatus: LiveData<PayEvent<String>>
        get() = _paystatus

    fun getUserOfClass(token: String, id: Long){
        viewModelScope.launch {
            _users.value = DataState.Loading
            _users.value = classRepository.getUserOfClass(token,id)
        }
    }

    fun updateStudentPayment(token: String, id: Int,classId: Long){
        viewModelScope.launch {
            val s: Flow<DataState<String>> = classRepository.updateStudentPayment(token, id)
            s.onEach {
                when (it) {
                    is DataState.Success -> {
                        _users.value = classRepository.getUserOfClass(token,classId)
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

    sealed class PayEvent<out R>() {
        data class Success<out T>(val data: T) : PayEvent<T>()
        data class Error(val data: String) : PayEvent<Nothing>()
        object Loading : PayEvent<Nothing>()
    }
}