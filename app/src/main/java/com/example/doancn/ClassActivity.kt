package com.example.doancn

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.doancn.Models.Classroom
import com.example.doancn.databinding.ActivityClassBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClassActivity : AppCompatActivity() {

    private val classViewModel: ClassViewModel by viewModels()

    private lateinit var binding: ActivityClassBinding
    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>
    private lateinit var options: ScanOptions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val classroom = intent.getSerializableExtra("targetClassroom") as Classroom
        classroom.let {
            Log.d("ClassActivity", "classroom $classroom")
            classViewModel.selectItem(classroom = classroom)
        }
        Log.d("context", this.toString())
        binding = ActivityClassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        setSupportActionBar(binding.mToolbar)
        val navController = findNavController(R.id.nav_host_fragment_activity_class)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_people, R.id.navigation_homework
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        }
        initQrCode()
        binding.floatingActionButton.setOnClickListener {
            classViewModel.createQR(classViewModel.classroom.value!!.classId, this)
        }
    }

    private fun initQrCode() {
        barcodeLauncher = registerForActivityResult(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Hủy quét", Toast.LENGTH_LONG).show()
            } else { // diem danh
                classViewModel.doAttendance(result.contents.toString(), this)

            }
        }
        options = ScanOptions()
        options.setBeepEnabled(false)
        options.setPrompt("quét mã QR để tiến hành điểm danh")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish();
        }
        return true
    }

    override fun onResume() {
        super.onResume()
    }
}