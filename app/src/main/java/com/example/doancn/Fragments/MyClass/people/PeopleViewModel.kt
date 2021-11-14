package com.example.doancn.Fragments.MyClass.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.UserMe
import com.example.doancn.Repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PeopleViewModel
@Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _users = MutableStateFlow<DataState<List<UserMe>?>>(DataState.Empty)
    val users: StateFlow<DataState<List<UserMe>?>> = _users

    private val statusMessage = MutableLiveData<String>()

    val message : LiveData<String>
        get() = statusMessage

    fun getUserOfClass(token: String, id: Long){
        viewModelScope.launch {
            _users.value = DataState.Loading
            _users.value = userRepository.getUserOfClass(token,id)
        }
    }

    fun updateStudentPayment(token: String, id: Int){
        val response = userRepository.updateStudentPayment(token, id)
        if(response.isSuccessful){
            statusMessage.value = "Cập nhật thành công"
        }
        else
            statusMessage.value = response.errorBody().toString()
    }
}