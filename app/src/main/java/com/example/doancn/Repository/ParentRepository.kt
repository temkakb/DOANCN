package com.example.doancn.Repository

import com.example.doancn.API.ProfileApi.IParentApi
import com.example.doancn.DI.DataState
import com.example.doancn.Models.Parent
import retrofit2.Response
import javax.inject.Inject

class ParentRepository @Inject constructor(
    private val parentapi : IParentApi
) {
    suspend fun getUserParents(token: String) : DataState<List<Parent>> {
        val response = parentapi.getUserParent(token)
        val result = response.body()
        return try {
            if (response.isSuccessful && result != null) {
                DataState.Success(result)
            } else {
                DataState.Error(response.errorBody().toString())
            }
        } catch (e: java.lang.Exception) {
            DataState.Error(e.message.toString())
        }
    }

    suspend fun deleteUserParent(token: String, id : Int): Response<Unit>{
        return parentapi.deleteParent(token,id)
    }

    suspend fun updateUserParent(token: String, id: Int, map: Map<String,String>): Response<Unit>{
        return parentapi.updateParent(token,id,map)
    }

    suspend fun addUserParent(token: String, map: Map<String,String>): Response<Unit>{
        return parentapi.addParent(token,map)
    }
}