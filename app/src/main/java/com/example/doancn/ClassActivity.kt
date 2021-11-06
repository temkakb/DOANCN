package com.example.doancn

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.doancn.Models.Classroom
import com.example.doancn.databinding.ActivityClassBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class ClassActivity : AppCompatActivity() {

    private val classViewModel: ClassViewModel by viewModels()

    private lateinit var binding: ActivityClassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            val classroom = intent.getSerializableExtra("targetClassroom") as Classroom
            classroom.let {
                Log.d("ClassActivity","classroom $classroom")
                classViewModel.selectItem(classroom = classroom)
        }
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
        navController.addOnDestinationChangedListener{controller, destination, arguments ->
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        }
     }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id==android.R.id.home) {
            finish();
        }
        return true
    }
    override fun onResume() {
        super.onResume()
    }
}