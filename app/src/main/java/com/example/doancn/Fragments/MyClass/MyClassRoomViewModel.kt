package com.example.doancn.Fragments.MyClass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Classroom
import com.example.doancn.Repository.ClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltViewModel
class MyClassRoomViewModel
@Inject constructor(
    private val classRepository: ClassRepository,
    @Named("auth_token") private val token: String?
) : ViewModel() {
    sealed class GetClassEvent<out R>() {
        data class Success<out T>(val data: T) : GetClassEvent<T>()
        data class Error(val data: String) : GetClassEvent<Nothing>()
        data class Empty<out T>(val data: T) : GetClassEvent<T>()
        object Loading : GetClassEvent<Nothing>()
    }

    private val _classList =
        MutableStateFlow<GetClassEvent<List<Classroom>>>(GetClassEvent.Empty(ArrayList()))
    val classList: StateFlow<GetClassEvent<List<Classroom>>> = _classList


    fun getClassList() {
        viewModelScope.launch(Dispatchers.IO) {
            _classList.value = GetClassEvent.Loading
            delay(1000)
            when (val listClassroom = classRepository.getListClass(token!!)) {
                is DataState.Success -> {
                    _classList.value = GetClassEvent.Success(listClassroom.data)

                }
                is DataState.Error -> {
                    _classList.value = GetClassEvent.Error(listClassroom.data)
                }
            }
        }
    }
}