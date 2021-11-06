package com.example.doancn.Fragments.MyClass

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.doancn.R
import com.example.doancn.Repository.AttendanceRepository
import com.example.doancn.Utilities.TokenManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyClassFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repository = AttendanceRepository()
        GlobalScope.launch {
            val qrcode = repository.getQrcodeToken(2L,TokenManager.userToken)
            Log.d("sdsasdasdasdadasd",qrcode.qrId)
        }

        return inflater.inflate(R.layout.fragment_myclass, container, false)
    }
}