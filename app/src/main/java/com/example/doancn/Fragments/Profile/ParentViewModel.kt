package com.example.doancn.Fragments.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Parent
import com.example.doancn.Repository.ParentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class ParentViewModel @Inject constructor(
    private val parentRepository: ParentRepository
) : ViewModel() {
    private val _parents = MutableStateFlow<DataState<List<Parent>?>>(DataState.Empty)
    val parents: StateFlow<DataState<List<Parent>?>> = _parents

    private val _changing = MutableStateFlow<DataState<String>>(DataState.Empty)
    val changing: StateFlow<DataState<String>> = _changing

    fun getUserParents(token: String){
        viewModelScope.launch {
            _parents.value = DataState.Loading
            _parents.value = parentRepository.getUserParents(token)
        }
    }

    fun updateUserParent(token: String, id:Int, map: Map<String, String>){
        viewModelScope.launch {
            _parents.value = DataState.Loading
            val response = parentRepository.updateUserParent(token,id,map)
            if(response.isSuccessful){
                _parents.value = parentRepository.getUserParents(token)
                _changing.value = DataState.Success("Cập nhật thông tin phụ huynh thành công")
            }
            else
                _changing.value = DataState.Error("Cập nhật thất bại")
        }
    }

    fun deleteUserParent(token: String, parent:Parent){
        viewModelScope.launch {
            _parents.value = DataState.Loading
            val response = parentRepository.deleteUserParent(token,parent.parentId)
            if(response.isSuccessful){
                _parents.value = parentRepository.getUserParents(token)
                _changing.value = DataState.Success("Xóa thành công phụ huynh ${parent.name}")
            }
            else
                _changing.value = DataState.Error("Xóa phụ huynh thất bại")
        }
    }

    fun addUserParent(token: String,map: Map<String,String>){
        viewModelScope.launch {
            _parents.value = DataState.Loading
            val response = parentRepository.addUserParent(token,map)
            if(response.isSuccessful){
                _changing.value = DataState.Success("Thêm phụ huynh thành công")
                _parents.value = parentRepository.getUserParents(token)
            }
            else
                _changing.value = DataState.Error("Thêm phụ huynh thất bại")
        }
    }


}