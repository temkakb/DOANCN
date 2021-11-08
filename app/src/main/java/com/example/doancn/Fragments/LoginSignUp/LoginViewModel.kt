package com.example.doancn.Fragments.LoginSignUp


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Account
import com.example.doancn.Repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginstatus = MutableStateFlow<DataState<Map<String, String>?>>(DataState.Empty)
    val loginstatus: StateFlow<DataState<Map<String, String>?>> = _loginstatus

    fun doLogin(account: Account) {
        viewModelScope.launch {  // coroutine scope viewmodel

            _loginstatus.value = DataState.Loading
            when (val datastate = authRepository.login(account)) {
                is DataState.Success -> _loginstatus.value = datastate
                is DataState.Error -> _loginstatus.value = datastate
            }
        }
    }

}