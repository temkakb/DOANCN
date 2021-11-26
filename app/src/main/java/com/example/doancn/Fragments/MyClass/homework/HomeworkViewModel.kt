package com.example.doancn.Fragments.MyClass.homework

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.HomeWorkX
import com.example.doancn.Repository.ClassRepository
import com.example.doancn.Retrofit.Urls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
@ExperimentalCoroutinesApi
class HomeworkViewModel
@Inject constructor(
    private val classRepository: ClassRepository,
    @Named("auth_token") private val token: String?,

)
    : ViewModel() {
    val SENDING_HOMEWORK = 6969
    val TEACHER_DOWNLOAD_HOMEWORK = 1000
    val STUDENT_DOWNLOAD_HOMEWORK = 1020
    val ALLOWCATE =4000
    private val _homeworks = MutableStateFlow<DataState<List<HomeWorkX>?>>(DataState.Empty)
    val homeworks: StateFlow<DataState<List<HomeWorkX>?>> = _homeworks
    private val _postHomeWorkStatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val postHomeWorkStatus: StateFlow<DataState<String>> = _postHomeWorkStatus
    private val _deleteHomeWorkStatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val deleteHomeWorkStatus: StateFlow<DataState<String>> = _deleteHomeWorkStatus
    private val _homeWork = MutableLiveData<HomeWorkX>()
    val homeWork: LiveData<HomeWorkX> get() = _homeWork
    private val _view = MutableLiveData<View>()
    val view: LiveData<View> get() = _view
    private val _downloadStatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val downloadStatus: StateFlow<DataState<String>> = _downloadStatus



    fun getData (classid :Long){
        viewModelScope.launch {
            _homeworks.value = DataState.Loading
            _homeworks.value= classRepository.getHomeWorks(token!!,
                classid)
            _homeworks.value=DataState.Empty
        }
    }

    fun deleteHomeWork(id:Long,homeWorkId: Long){
        viewModelScope.launch {
        _deleteHomeWorkStatus.value=DataState.Loading
            _deleteHomeWorkStatus.value=classRepository.deleteHomeWork(id,homeWorkId, token!!)
            _deleteHomeWorkStatus.value=DataState.Empty
        }
    }

    fun postHomeWork (deadline: String,filename: String , file: ByteArray,classid: Long){
        viewModelScope.launch {
            Thread {
                val buffer: ByteBuffer = ByteBuffer.allocate(ALLOWCATE)
                var buffer2: ByteBuffer = ByteBuffer.allocate(50)
                val deadLineBytes = deadline.toByteArray()
                val fileNameBytes =filename.toByteArray()
                val tokenBytes = token!!.substring(7).toByteArray()
                _postHomeWorkStatus.value=DataState.Loading
                val hA = InetSocketAddress(Urls.url2,  Urls.port)
                val client= SocketChannel.open(hA)
                // send header
                buffer.putInt(SENDING_HOMEWORK)
                buffer.putLong(classid)
                buffer.putInt(tokenBytes.size)
                buffer.put(tokenBytes)
                buffer.putInt(deadLineBytes.size)
                buffer.put(deadLineBytes)
                buffer.putInt(fileNameBytes.size)
                buffer.put(fileNameBytes)
                buffer.putInt(file.size)
                buffer.flip()
                client.write(buffer)
                // read response
                client.read(buffer2)
                buffer2.flip()
                var bytearray = ByteArray(buffer2.remaining())
                buffer2.get(bytearray)
                var smg = String(bytearray)
                if(smg.equals("Xác thực thành công")) {
                    val data = ByteBuffer.wrap(file)
                    while (data.hasRemaining()){
                        client.write(data)
                    }
                    buffer2= ByteBuffer.allocate(50)
                    client.read(buffer2)
                    buffer2.flip()
                    bytearray = ByteArray(buffer2.remaining())
                    buffer2.get(bytearray)
                    smg= String(bytearray)
                    _postHomeWorkStatus.value=DataState.Success(smg)
                }
                else
                    _postHomeWorkStatus.value=DataState.Error(smg)

            }.start()

        }
    }

    fun teacherDownLoadHomeWork(classid: Long,uri : Uri,context: Context){
            viewModelScope.launch {
                Thread {
                    _downloadStatus.value = DataState.Loading
                    val buffer: ByteBuffer = ByteBuffer.allocate(ALLOWCATE)
                    val buffer2: ByteBuffer = ByteBuffer.allocate(homeWork.value!!.sizeInByte)
                    val tokenBytes = token!!.substring(7).toByteArray()
                    val hA = InetSocketAddress(Urls.url2, Urls.port)
                    val client = SocketChannel.open(hA)
                    buffer.putInt(TEACHER_DOWNLOAD_HOMEWORK)
                    buffer.putLong(classid)
                    buffer.putInt(tokenBytes.size)
                    buffer.put(tokenBytes)
                    buffer.putLong(homeWork.value!!.fileId)
                    buffer.flip()
                    client.write(buffer)
                    buffer2.clear()
                    try {
                        while (buffer2.hasRemaining()) {
                            client.read(buffer2)
                        }
                        client.close()

                    } catch (ex: IOException) {
                        client.close()
                        _downloadStatus.value = DataState.Error("Tải thất bại")
                    }

                    val file = ByteArray(homeWork.value!!.sizeInByte)
                    buffer2.flip()
                    buffer2.get(file)



                  val out = context.contentResolver.openOutputStream(uri)
                    out!!.write(file)
                    out.close()


                    _downloadStatus.value = DataState.Success("Tải thành công")

                }.start()
            }

    }
    fun studentDownLoadHomeWork (classid: Long,uri : Uri,context: Context){
        viewModelScope.launch {
            Thread {
                _downloadStatus.value = DataState.Loading
                val buffer: ByteBuffer = ByteBuffer.allocate(ALLOWCATE)
                val buffer2: ByteBuffer = ByteBuffer.allocate(homeWork.value!!.sizeInByte)
                val tokenBytes = token!!.substring(7).toByteArray()
                val hA = InetSocketAddress(Urls.url2, Urls.port)
                val client = SocketChannel.open(hA)
                buffer.putInt(STUDENT_DOWNLOAD_HOMEWORK)
                buffer.putLong(classid)
                buffer.putInt(tokenBytes.size)
                buffer.put(tokenBytes)
                buffer.putLong(homeWork.value!!.fileId)
                buffer.flip()
                client.write(buffer)
                buffer2.clear()
                try {
                    while (buffer2.hasRemaining()) {
                        client.read(buffer2)
                    }
                    client.close()

                } catch (ex: IOException) {
                    client.close()
                    _downloadStatus.value = DataState.Error("Tải thất bại")
                }

                val file = ByteArray(homeWork.value!!.sizeInByte)
                buffer2.flip()
                buffer2.get(file)
                val checkfile = File(uri.path)
                if (checkfile.exists()){
                    Log.d("exsit","yessss")
                }
                val out = context.contentResolver.openOutputStream(uri)
                out!!.write(file)
                out.close()
                _downloadStatus.value = DataState.Success("Tải thành công")

            }.start()
        }
    }
    fun setEmptyHomeWorkStatus(){
        viewModelScope.launch {
            _postHomeWorkStatus.value=DataState.Empty
        }
    }
    fun setEmptyDownLoadStatus(){
        viewModelScope.launch {
            _downloadStatus.value=DataState.Empty
        }

    }

    fun selectitem (homeWorkX: HomeWorkX){

        _homeWork.value=homeWorkX
    }
    fun setView (view: View){
        _view.value=view
    }






}