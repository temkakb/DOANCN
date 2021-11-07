package com.example.doancn.Utilities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.example.doancn.Models.QrCodeX
import com.example.doancn.R
import com.example.doancn.Repository.AttendanceRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.dialog_qrcode.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class QrCodeManager(var token: String) {
    val repository = AttendanceRepository()
    var qrcode: QrCodeX? = null
    var multiFormatWriter: MultiFormatWriter = MultiFormatWriter()
    val encode = BarcodeEncoder()
    var classId: Long = -1

    fun getQrCode(context: Context, classId: Long, token: String) {

        GlobalScope.launch(Dispatchers.IO) {
            try {

                Log.d("QrCodeManager", "token : $token")
                Log.d("context", context.toString())
                this@QrCodeManager.classId = classId
                qrcode = repository.getQrcodeToken(classId, token)
                withContext(Dispatchers.Main) {
                    showDialog(context)
                }
            } catch (e: HttpException) {
                val jObjError = JSONObject(e.response()?.errorBody()!!.string())
                val msg = jObjError.get("message")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        msg.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showDialog(context: Context) {
        val dialog = Dialog(context)
        //dialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        dialog.setContentView(R.layout.dialog_qrcode)
        dialog.btn_close.setOnClickListener {
            dialog.dismiss()
        }
        Log.d("qrcodeid", qrcode!!.qrId)
        val countDownTimer = object : CountDownTimer(qrcode!!.timeleft.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                dialog.countdown.text = context.resources.getString(
                    R.string.formatted_time,
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                )
            }

            override fun onFinish() {
                dialog.dismiss()
                val dialogalert = AlertDialog.Builder(context)
                dialogalert.setTitle(context.resources.getString(R.string.qrcode_expired_title))
                    .setMessage(context.resources.getString(R.string.qrcode_expired_msg))
                    .setPositiveButton(context.resources.getString(R.string.qrcode_expired_yes)) { dialoginterface, int ->
                        getQrCode(context, classId = classId, token)
                    }
                    .setNegativeButton(context.resources.getString(R.string.qrcode_expired_no)) { dialoginterface, int ->
                        dialoginterface.dismiss()
                    }.show()
            }
        }
        try {
            val bitMatrix = multiFormatWriter.encode(qrcode!!.qrId, BarcodeFormat.QR_CODE, 700, 700)
            val bitmap = encode.createBitmap(bitMatrix)
            dialog.iv_qrcode.setImageBitmap(bitmap)

        } catch (e: WriterException) {
            e.printStackTrace()
        }
        countDownTimer.start()
        dialog.show()
    }

    fun doAttendace(classId: Long, qrcode: String, token: String, context: Context) {
        GlobalScope.launch {
            try {
                repository.doAttendance(classId, qrcode, token)
            } catch (e: HttpException) {
                val jObjError = JSONObject(e.response()?.errorBody()!!.string())
                val msg = jObjError.get("message")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        msg.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}