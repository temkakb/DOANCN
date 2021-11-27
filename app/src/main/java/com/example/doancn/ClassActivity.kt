package com.example.doancn

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.example.doancn.ClassViewModel.ClassEvent.*
import com.example.doancn.Fragments.MyClass.more.BottomSheetItem
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.classModel.ClassQuest
import com.example.doancn.databinding.ActivityClassBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ClassActivity : AppCompatActivity(), IClassActivity {

    private val classViewModel: ClassViewModel by viewModels()

    private lateinit var binding: ActivityClassBinding
    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>
    private lateinit var options: ScanOptions
    private lateinit var navController: NavController

    @SuppressLint("RestrictedApi")
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
        navController = findNavController(R.id.nav_host_fragment_activity_class)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_people,
                R.id.navigation_homework,
                R.id.navigation_bottom_sheet
            )
        )
        navView.background = null
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            when (destination.id) {
                //  R.id.navigation_home -> navController.backStack.clear()
                R.id.updateFragment -> {
                    navView.visibility = View.INVISIBLE
                    binding.floatingActionButton.visibility = View.INVISIBLE
                    setMargins(binding.fragContainer, 0, 0, 0, 0)
                }
                R.id.sectionFragment -> {
                    navView.visibility = View.INVISIBLE
                    binding.floatingActionButton.visibility = View.INVISIBLE
                    setMargins(binding.fragContainer, 0, 0, 0, 0)
                }
                else -> {
                    navView.visibility = View.VISIBLE
                    binding.floatingActionButton.visibility = View.VISIBLE
                    setMargins(binding.fragContainer, 0, 0, 0, 145)
                }
            }
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun handleBottomSheetItem(item: BottomSheetItem) {
        when (item.type) {
            1 -> {
                MaterialDialog(this).show {
                    title(text = "Thông báo")
                    message(text = "Hủy lớp học này?")
                    positiveButton(R.string.agree) { dialog ->
                        classViewModel.deleteCurrentClass()
                        observeDeleteState()
                    }
                    negativeButton(R.string.disagree) { dialog ->
                    }
                }
            }
            2 -> {
                val bundle = Bundle()
                bundle.putSerializable("targetUpdateClass", classViewModel.classroom.value)
                //navController.popBackStack()
                navController.navigate(
                    R.id.action_navigation_bottom_sheet_to_updateFragment, bundle
                )
            }
            3 -> {
                navController.navigate(
                    R.id.action_navigation_bottom_sheet_to_sectionFragment
                )
            }
        }
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private fun observeDeleteState() {
        lifecycleScope.launchWhenCreated {
            classViewModel.deleteState.collect { event ->
                when (event) {
                    is Success -> {
                        startActivity(
                            Intent(
                                this@ClassActivity,
                                MainActivity::class.java
                            ).apply { this.putExtra("backToListClass", true) })
                        Toast.makeText(this@ClassActivity, "success", Toast.LENGTH_SHORT).show()

                    }
                    is Empty -> {
                        Toast.makeText(this@ClassActivity, "empty data", Toast.LENGTH_SHORT).show()
                    }
                    is Error -> {
                        Toast.makeText(
                            this@ClassActivity,
                            "error: ${event.data}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    is Loading -> {
                        Toast.makeText(this@ClassActivity, "data loading", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }

    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.layoutParams = p
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val REQUEST_CODE = 300
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val latitude = data?.getDoubleExtra("latitude", -1.0)
            val longitude = data?.getDoubleExtra("longitude", -1.0)
            Log.d("onActivityResult", "latitude: $latitude, longitude: $longitude")
            val geocoder = Geocoder(this)
            latitude?.let { longitude?.let { it1 -> geocoder.getFromLocation(it, it1, 1) } }.apply {
                val item = this?.get(0)?.let {
                    ClassQuest.Location(
                        city = it.locality,
                        coordinateX = latitude!!,
                        coordinateY = longitude!!,
                        address = ""
                    )
                }
                Log.d("TAG", item.toString())

                item?.let { classViewModel.selectItem(it) }
                Log.d("onActivityResult", classViewModel.selectedItem.value.toString())
            }


        }
    }
}

interface IClassActivity {
    fun handleBottomSheetItem(item: BottomSheetItem)
}

