package com.example.doancn.Fragments.MyClass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.doancn.R
import com.example.doancn.Utilities.QrCodeManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class MyClassFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return inflater.inflate(R.layout.fragment_myclass, container, false)
    }


    private fun doAttandance (){
        val barcodeLauncher = registerForActivityResult(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Hủy quét", Toast.LENGTH_LONG).show()
            } else { // diem danh

            }
        }
        val options = ScanOptions()
        options.setBeepEnabled(false)
        options.setPrompt("quét mã QR để tiến hành điểm danh")
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        barcodeLauncher.launch(options)
    }
}