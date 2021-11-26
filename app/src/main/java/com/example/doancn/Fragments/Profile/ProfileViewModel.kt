package com.example.doancn.Fragments.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.UserMe
import com.example.doancn.Repository.UserRepository
import com.example.doancn.ViewModels.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _user = MutableStateFlow<DataState<UserMe?>>(DataState.Empty)
    val user: StateFlow<DataState<UserMe?>> = _user

    private val _changingstatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val changingstatus: StateFlow<DataState<String>> = _changingstatus

    private val _changing = MutableStateFlow<DataState<String>>(DataState.Empty)
    val changing: StateFlow<DataState<String>> = _changing

    fun getUserMe(token: String){
        viewModelScope.launch {
            _user.value = DataState.Loading
            _user.value = userRepository.getme(token)
        }
    }

    fun updateImgUser(token: String, map: Map<String, String>, model: UserViewModel){
        viewModelScope.launch {
            _changingstatus.value = DataState.Loading
            _changingstatus.value = userRepository.updateimg(token,map)
            if(_changingstatus.value is DataState.Success){
                model.user!!.image = map["imgBase64"].toString()
            }
        }
    }

    fun updateUser(token: String, user: UserMe, model: UserViewModel){
        viewModelScope.launch {
            _changing.value = DataState.Loading
            _changing.value = userRepository.updateuser(token,user)
            if(_changing.value is DataState.Success){
                model.user = user
            }
        }
    }

    fun updatePassUser(token: String, map: Map<String, String>){
        viewModelScope.launch {
            _changing.value = DataState.Loading
            _changing.value = userRepository.updatePassUser(token,map)
        }
    }

}