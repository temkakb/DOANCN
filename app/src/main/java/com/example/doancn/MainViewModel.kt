package com.example.doancn

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class MainViewModel
@Inject constructor(

    @Named("auth_token") val token: String?,
    @Named("user_role") val role: String?,
    val authRepository: AuthRepository
) : ViewModel() {

    private val _validatetoken = MutableStateFlow<DataState<String>>(DataState.Empty)
    val validatetoken: StateFlow<DataState<String>> = _validatetoken


    fun doValidateToken() {
        viewModelScope.launch {

            viewModelScope.launch {
                Log.d("tokendasdada", token!!)
                val jsontoken = mapOf(pair = Pair("token", token!!.substring(7)))
                when (val dataState = authRepository.validate(jsontoken)) {
                    is DataState.Success -> _validatetoken.value = dataState
                    is DataState.Error -> _validatetoken.value = dataState
                }
            }
        }
    }

}
