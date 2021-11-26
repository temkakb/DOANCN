package com.example.doancn.Fragments.CreateClass


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.classModel.ClassQuest
import com.example.doancn.Repository.ClassRepository
import com.example.doancn.Repository.SubjectsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltViewModel
class CreateClassViewModel
@Inject
constructor(
    private val classRepository: ClassRepository,
    private val subjectRepository: SubjectsRepository,
    @Named("auth_token") val token: String?,
    @Named("user_role") val role: String?
) : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<ClassQuest.Location>()
    val selectedItem: LiveData<ClassQuest.Location> get() = mutableSelectedItem
    fun selectItem(item: ClassQuest.Location) {
        mutableSelectedItem.value = item
    }


    private val _createClassResponse: MutableLiveData<CreateClassEvent<String>> = MutableLiveData()
    val createClassResponse: LiveData<CreateClassEvent<String>>
        get() = _createClassResponse

    fun resetResponse() {
        _createClassResponse.value = CreateClassEvent.Empty
    }

    fun createClassroom(classRoom: ClassQuest) {
        viewModelScope.launch {
            val s: Flow<DataState<String>> = classRepository.createClassroom(classRoom, token!!)
            s.onEach {
                when (it) {
                    is DataState.Success -> {
                        _createClassResponse.value = CreateClassEvent.Success(it.data)
                    }
                    is DataState.Error -> {
                        _createClassResponse.value = CreateClassEvent.Error(it.data)
                    }
                    is DataState.Loading -> {
                        _createClassResponse.value = CreateClassEvent.Loading
                    }
                    else -> Log.d("TAG", "No data")
                }
            }.launchIn(viewModelScope)
        }
    }

}

sealed class CreateClassEvent<out R>() {
    data class Success<out T>(val data: T) : CreateClassEvent<T>()
    data class Error(val data: String) : CreateClassEvent<Nothing>()
    object Loading : CreateClassEvent<Nothing>()
    object Empty : CreateClassEvent<Nothing>()
}


//    private val _dataState: MutableLiveData<DataState<List<ClassQuest.Subject>>> = MutableLiveData()
//    val dataState: LiveData<DataState<List<ClassQuest.Subject>>>
//        get() = _dataState


//    fun getSubject() {
//        viewModelScope.launch {
//            subjectRepository.getSubject(token)
//                .onEach { dataState ->
//                    _dataState.value = dataState
//                }
//                .launchIn(viewModelScope)
//            Log.d("TAG", _dataState.toString())
//        }
//}