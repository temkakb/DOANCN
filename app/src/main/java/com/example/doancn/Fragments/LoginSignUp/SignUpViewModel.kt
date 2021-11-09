package com.example.doancn.Fragments.LoginSignUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.AccountSignUp
import com.example.doancn.Repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SignUpViewModel
@Inject constructor(
    private val authRepository: AuthRepository,
    private val accountSignUp: AccountSignUp

) : ViewModel() {


    private val _signupstatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val signupstatus: StateFlow<DataState<String>> = _signupstatus


    fun doSigup() {
        viewModelScope.launch {
            _signupstatus.value = DataState.Loading
            _signupstatus.value = authRepository.signup(accountSignUp)
        }
    }


}