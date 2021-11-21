package com.example.doancn.Fragments.MyClass.homework

import android.util.Log
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
    val ALLOWCATE =2600000
    private val _homeworks = MutableStateFlow<DataState<List<HomeWorkX>?>>(DataState.Empty)
    val homeworks: StateFlow<DataState<List<HomeWorkX>?>> = _homeworks

    private val _postHomeWorkStatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val postHomeWorkStatus: StateFlow<DataState<String>> = _postHomeWorkStatus

    fun getData (classid :Long){
        viewModelScope.launch {
            _homeworks.value = DataState.Loading
            _homeworks.value= classRepository.getHomeWorks(token!!,
                classid)
        }
    }

    fun postHomeWork (deadline: String,filename: String , file: ByteArray,classid: Long){
        viewModelScope.launch {
            Thread {
                var buffer: ByteBuffer = ByteBuffer.allocate(ALLOWCATE)
                var buffer2: ByteBuffer = ByteBuffer.allocate(50)
                val deadLineBytes = deadline.toByteArray()
                val fileNameBytes =filename.toByteArray()
                val tokenBytes = token!!.substring(7).toByteArray()
                _postHomeWorkStatus.value=DataState.Loading
                val hA = InetSocketAddress(Urls.url2, 6969)
                val client= SocketChannel.open(hA)
                buffer.putInt(SENDING_HOMEWORK)
                buffer.putLong(classid)
                buffer.putInt(tokenBytes.size)
                buffer.put(tokenBytes)
                buffer.putInt(deadLineBytes.size)
                buffer.put(deadLineBytes)
                buffer.putInt(fileNameBytes.size)
                buffer.put(fileNameBytes)
                buffer.put(file)
                buffer.flip()
                client.write(buffer)
                val readbyte = client.read(buffer2)
                buffer2.flip()
                Log.d("filelengh",file.size.toString())
                var bytearray = ByteArray(buffer2.remaining());
                buffer2.get(bytearray)
                val smg = String(bytearray)
                if(smg.equals("Thêm bài tập thành công"))
                _postHomeWorkStatus.value=DataState.Success(smg)
                else
                    _postHomeWorkStatus.value=DataState.Error(smg)

            }.start()

        }
    }




}