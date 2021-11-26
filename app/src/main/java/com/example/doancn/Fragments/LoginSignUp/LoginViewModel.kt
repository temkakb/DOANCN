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

    var email: String? = null
    private val _loginstatus = MutableStateFlow<DataState<Map<String, String>?>>(DataState.Empty)
    val loginstatus: StateFlow<DataState<Map<String, String>?>> = _loginstatus
    private val _forgotPasswordStatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val forgotPasswordStatus: StateFlow<DataState<String>> = _forgotPasswordStatus
    private val _validateCode = MutableStateFlow<DataState<String>>(DataState.Empty)
    val validateCode: StateFlow<DataState<String>> = _validateCode


    fun doGoogleLogin(map: Map<String,String>) {
        viewModelScope.launch {
            _loginstatus.value = DataState.Loading
            _loginstatus.value = authRepository.doGoogleLogin(map)

        }
    }

    fun doSigupGoogle(map: Map<String, String>) {
        viewModelScope.launch {
            _loginstatus.value = DataState.Loading
            _loginstatus.value = authRepository.doGoogleSignup(map)
        }
    }

    fun doLogin(account: Account) {
        viewModelScope.launch {  // coroutine scope viewmodel

            _loginstatus.value = DataState.Loading
            _loginstatus.value = authRepository.login(account)

        }
    }

    fun requestForgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordStatus.value = DataState.Loading
            _forgotPasswordStatus.value = authRepository.forGotPassword(email, null)
            _forgotPasswordStatus.value = DataState.Empty

        }

    }

    fun validateCode(code: String) {
        viewModelScope.launch {
            _validateCode.value = DataState.Loading
            _validateCode.value = authRepository.forGotPassword(email!!, code)
            _forgotPasswordStatus.value = DataState.Empty
        }

    }

}