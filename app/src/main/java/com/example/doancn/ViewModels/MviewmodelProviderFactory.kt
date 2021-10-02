package com.example.doancn.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doancn.Models.AccountSignUp

class MviewmodelProviderFactory(val account : AccountSignUp, val context : Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SignUpManagerViewModel(account, context) as T

    }
}