package com.example.doancn.Fragments.MyClass.homework

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.HomeWorkX
import com.example.doancn.Models.SubmissionX
import com.example.doancn.Repository.ClassRepository
import com.example.doancn.Retrofit.Urls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
@ExperimentalCoroutinesApi
class SubmissionViewModel
@Inject constructor(
    private val classRepository: ClassRepository,
    @Named("auth_token") private val token: String?,

    )
    : ViewModel() {
    private val SENDING_SUBMISSION = 6970
    private val TEACHER_DOWNLOAD_SUBMISSION = 1010
    val ALLOWCATE =2600

    private val _submissions= MutableStateFlow<DataState<List<SubmissionX>?>>(DataState.Empty)
    val submissions: StateFlow<DataState<List<SubmissionX>?>> = _submissions
    private val _submission= MutableStateFlow<DataState<SubmissionX?>>(DataState.Empty)
    val submission: StateFlow<DataState<SubmissionX?>> = _submission
    private val _statusRequest= MutableStateFlow<DataState<String>>(DataState.Empty)
    val statusRequest: StateFlow<DataState<String>> = _statusRequest
    private val _submissionSelected = MutableLiveData<SubmissionX>()
    val submissionSelected: LiveData<SubmissionX> get() = _submissionSelected
    private val _downloadStatus = MutableStateFlow<DataState<String>>(DataState.Empty)
    val downloadStatus: StateFlow<DataState<String>> = _downloadStatus


    fun getData(id: Long,homeworkId : Long){
        viewModelScope.launch {
            _submissions.value = DataState.Loading
            _submissions.value= classRepository.getSubmissions(id,homeworkId, token!!)
            _submissions.value=DataState.Empty
        }
    }
    fun getSubmission(id: Long,homeworkId: Long){
        viewModelScope.launch {
            _submission.value=DataState.Loading
            _submission.value=classRepository.getSubmission(id,homeworkId,token!!)
            _submission.value= DataState.Empty
        }
    }
   fun deleteSubmission(id: Long,classId: Long){
       viewModelScope.launch {
           _statusRequest.value=DataState.Loading
           _statusRequest.value=classRepository.deleteSubmission(id,classId, token!!)
           _statusRequest.value=DataState.Empty
       }
   }

    fun postSubmission(id:Long,homeworkId: Long,filename:String,file: ByteArray) {
        viewModelScope.launch {
            Thread {
                val buffer: ByteBuffer = ByteBuffer.allocate(ALLOWCATE)
                var buffer2: ByteBuffer = ByteBuffer.allocate(50)
                val fileNameBytes = filename.toByteArray()
                val tokenBytes = token!!.substring(7).toByteArray()
                _statusRequest.value = DataState.Loading
                val hA = InetSocketAddress(Urls.url2,  Urls.port)
                val client = SocketChannel.open(hA)
                buffer.putInt(SENDING_SUBMISSION)
                buffer.putLong(id)
                buffer.putInt(tokenBytes.size)
                buffer.put(tokenBytes)
                buffer.putLong(homeworkId)
                buffer.putInt(fileNameBytes.size)
                buffer.put(fileNameBytes)
                buffer.putInt(file.size)
                buffer.flip()
                client.write(buffer)


                client.read(buffer2)
                buffer2.flip()
                var bytearray = ByteArray(buffer2.remaining())
                buffer2.get(bytearray)
                var smg = String(bytearray)
                if (smg.equals("Xác thực thành công")) {
                    val data = ByteBuffer.wrap(file)
                    while (data.hasRemaining()) {
                        client.write(data)
                    }
                    buffer2= ByteBuffer.allocate(50)
                    client.read(buffer2)
                    buffer2.flip()
                    bytearray = ByteArray(buffer2.remaining())
                    buffer2.get(bytearray)
                    smg= String(bytearray)
                    _statusRequest.value=DataState.Success(smg)
                }
                else
                    _statusRequest.value=DataState.Error(smg)
            }.start()
        }
    }

    fun teacherDownloadSubmission (id: Long, classId: Long,homeWork :HomeWorkX, submissionX: SubmissionX, uri : Uri, context: Context){
        viewModelScope.launch {
            Thread {
                _downloadStatus.value = DataState.Loading
                val buffer: ByteBuffer = ByteBuffer.allocate(ALLOWCATE)
                val buffer2: ByteBuffer = ByteBuffer.allocate(submissionX.sizeInByte)
                val tokenBytes = token!!.substring(7).toByteArray()
                val hA = InetSocketAddress(Urls.url2, Urls.port)
                val client = SocketChannel.open(hA)
                buffer.putInt(TEACHER_DOWNLOAD_SUBMISSION)
                buffer.putLong(classId)
                buffer.putInt(tokenBytes.size)
                buffer.put(tokenBytes)
                buffer.putLong(homeWork.fileId)
                buffer.putLong(submissionX.fileId)
                buffer.flip()
                client.write(buffer)
                buffer2.clear()
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

                val file = ByteArray(submissionX.sizeInByte)
                buffer2.flip()
                buffer2.get(file)

                val out = context.contentResolver.openOutputStream(uri)
                out!!.write(file)
                out.close()
                _downloadStatus.value = DataState.Success("Tải thành công")
            }
        }


    }
    fun setEmptyStatus(){
        _statusRequest.value=DataState.Empty
    }
    fun setSubmissionSelected(submissionX: SubmissionX){
        _submissionSelected.value=submissionX;
    }

}