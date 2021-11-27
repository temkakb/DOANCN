//package com.example.doancn.Utilities
//
//import android.app.AlertDialog
//import android.app.Dialog
//import android.content.Context
//import android.os.CountDownTimer
//import android.util.Log
//import android.widget.Toast
//import com.example.doancn.Models.QrCodeX
//import com.example.doancn.R
//import com.example.doancn.Repository.AttendanceRepository
//import com.google.zxing.BarcodeFormat
//import com.google.zxing.MultiFormatWriter
//import com.google.zxing.WriterException
//import com.journeyapps.barcodescanner.BarcodeEncoder
//import kotlinx.android.synthetic.main.dialog_qrcode.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import retrofit2.HttpException
//import java.util.concurrent.TimeUnit
//
//class QrCodeManager(var token: String) {
//    val repository = AttendanceRepository()
//    var qrcode: QrCodeX? = null
//    var multiFormatWriter: MultiFormatWriter = MultiFormatWriter()
//    val encode = BarcodeEncoder()
//    var classId: Long = -1
//
//    fun getQrCode(classId: Long, token: String, context: Context) {
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//
//                Log.d("QrCodeManager", "token : $token")
//                Log.d("context", context.toString())
//                this@QrCodeManager.classId = classId
//                qrcode = repository.getQrcodeToken(classId, token)
//                withContext(Dispatchers.Main) {
//                    showDialog(context)
//                }
//            } catch (e: HttpException) {
//
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        context,
//                        e.response()?.errorBody()!!.string(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//
//    fun doAttendace(classId: Long, qrcode: String, token: String, context: Context) {
//        GlobalScope.launch {
//            try {
//                repository.doAttendance(classId, qrcode, token)
//            } catch (e: HttpException) {
//
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        context,
//                        e.response()?.errorBody().toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//}