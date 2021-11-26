package com.example.doancn.Fragments.MyClass.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Fragments.MyClass.people.PeopleViewModel.GetListStudentEvent.*
import com.example.doancn.Models.User
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
class PeopleViewModel
@Inject constructor(
    private val classRepository: ClassRepository,
    @Named("auth_token") private val token: String?
) : ViewModel() {
    sealed class GetListStudentEvent<out R>() {
        data class Success<out T>(val data: T) : GetListStudentEvent<T>()
        data class Error(val data: String) : GetListStudentEvent<Nothing>()
        data class Empty<out T>(val data: T) : GetListStudentEvent<T>()
        object Loading : GetListStudentEvent<Nothing>()


    }

    private val _studentList =
        MutableStateFlow<GetListStudentEvent<List<User>>>(
            Empty(ArrayList())
        )
    val studentList: StateFlow<GetListStudentEvent<List<User>>> = _studentList
    fun getStudentList(classId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _studentList.value = Loading
            delay(1000)
            when (val listStudent = classRepository.getListStudent(token!!, classId)) {
                is DataState.Success -> {
                    _studentList.value = Success(listStudent.data)

                }
                is DataState.Error -> {
                    _studentList.value = Error(listStudent.data)
                }
            }
        }
    }
}