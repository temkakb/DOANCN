package com.example.doancn.Fragments.Home

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
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _user = MutableStateFlow<DataState<UserMe?>>(DataState.Empty)
    val user: StateFlow<DataState<UserMe?>> = _user

    fun getUserMe(token: String){
        viewModelScope.launch {
            _user.value = DataState.Loading
            _user.value = userRepository.getme(token)
        }
    }
}