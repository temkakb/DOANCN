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
    @Named("auth_token") private val token: String
) : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<ClassQuest.Location>()
    val selectedItem: LiveData<ClassQuest.Location> get() = mutableSelectedItem
    fun selectItem(item: ClassQuest.Location) {
        mutableSelectedItem.value = item
    }

    private val _dataState: MutableLiveData<DataState<List<ClassQuest.Subject>>> = MutableLiveData()

    val dataState: LiveData<DataState<List<ClassQuest.Subject>>>
        get() = _dataState
    private val _createClassResponse: MutableLiveData<DataState<String>> = MutableLiveData()
    val createClassResponse: LiveData<DataState<String>>
        get() = _createClassResponse

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

    fun createClassroom(classRoom: ClassQuest) {
        viewModelScope.launch {
            Log.d("TAG", classRepository.toString())
            val s: Flow<DataState<String>> = classRepository.createClassroom(classRoom, token)
            s.onEach {
                Log.d("createClassroom_when", it.toString())
                when (it) {
                    is DataState.Success -> {
                        Log.d("DataState.Success", it.toString())
                        _createClassResponse.value = DataState.Success(it.data)
                    }
                    is DataState.Error -> {
                        Log.d("DataState.Error", it.toString())
                        _createClassResponse.value = DataState.Error(it.data)
                    }
                    else -> Log.d("TAG", "No data")
                }
            }.launchIn(viewModelScope)

        }
    }

}