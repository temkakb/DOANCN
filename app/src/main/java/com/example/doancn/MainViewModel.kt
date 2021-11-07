package com.example.doancn

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class MainViewModel
@Inject constructor(
    @Named("auth_token") val token: String,
    @Named("user_role") val role: String
) : ViewModel()
