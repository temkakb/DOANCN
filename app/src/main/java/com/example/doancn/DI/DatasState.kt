package com.example.doancn.DI

sealed class DataState<out R> {

    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val data: String) : DataState<Nothing>()
    data class ErrorWithException(val exception: Exception) : DataState<Nothing>()
    object Loading : DataState<Nothing>()
    object Emty : DataState<Nothing>()
}