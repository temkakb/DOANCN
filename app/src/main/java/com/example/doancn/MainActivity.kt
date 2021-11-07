package com.example.doancn

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.doancn.Fragments.CreateClass.CreateClassViewModel
import com.example.doancn.Fragments.JoinClass.JoinClassFragment
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
import com.example.doancn.Models.classModel.ClassQuest
import com.example.doancn.Repository.AuthRepository
import com.example.doancn.Retrofit.RetrofitManager
import com.example.doancn.Utilities.TokenManager
import com.example.doancn.ViewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), IMainActivity {

    private lateinit var navcontroller: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    private val viewModel: CreateClassViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Action bar
        setSupportActionBar(mToolbar)
        val toogle =
            ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
        // Navigation drawer
        nav_view.menu.findItem(R.id.nav_home).isChecked = true
        //navcontroller
        setUpNavigation()
        // khai báo UserViewModel
        val model: UserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val sharedprefernces = getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
        val token: String? = sharedprefernces.getString("token", null)
        val intent = Intent(this, LoginRegisterActivity::class.java)
        if (token == null) { // CODE CHAY CHO MAU, co gi chu improve nha
            startActivity(intent)
            finish()
        } else {
            GlobalScope.launch {
                try {
                    val auth = AuthRepository()
                    val map = HashMap<String, String>()
                    map["token"] = token
                    Log.i("MyToken", token)
                    auth.validate(map)
                    getMyUser(token, model)
                    if (model.user != null) {
                        Log.i("Username", model.user!!.name)
                        runOnUiThread {
                            val header: View = nav_view.getHeaderView(0)
                            header.user_name.text = model.user!!.name
                            header.user_email.text = model.user!!.account.email
                            if (model.user!!.image != null) {
                                val imgDecode: ByteArray =
                                    Base64.getDecoder().decode(model.user!!.image)
                                val bmp =
                                    BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
                                header.user_image.setImageBitmap(bmp)
                            } else {
                                when (model.user!!.gender.genderID) {
                                    1 -> {
                                        header.user_image.setImageResource(R.drawable.man)
                                    }
                                    2 -> {
                                        header.user_image.setImageResource(R.drawable.femal)
                                    }
                                    3 -> {
                                        header.user_image.setImageResource(R.drawable.orther)
                                    }
                                }
                            }

                        }
                    }

                    TokenManager.apply {
                        getpublickey(token)
                        readrolefromtokenJws()
                        userToken= "Bearer $token"
                        if (role == "STUDENT") {
                            runOnUiThread {
                                val navmenu: Menu = nav_view.menu
                                navmenu.findItem(R.id.nav_createclass).isVisible = false
                            }
                        } else if (role == "TEACHER") {
                            runOnUiThread {
                                val navmenu: Menu = nav_view.menu
                                navmenu.findItem(R.id.nav_joinclass).isVisible = false
                            }
                        }
                    }
                }
                // token ko hop le
                catch (e: retrofit2.HttpException) {
                    sharedprefernces.edit().clear().apply()
                    startActivity(intent)
                    finish()
                }
            }
        }
        /***-----------------xem co token duoi sharedpre pho` ran ko. neu co thi validate thu-------------------- ***/

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayUseLogoEnabled(true)
        actionBar?.setLogo(R.drawable.ic_baseline_home_24)
        actionBar?.title = "Trang chủ"

    }

    private fun getMyUser(token: String, model: UserViewModel) {

        val callSync: Call<UserMe> = RetrofitManager.userapi.getme("Bearer $token")
        try {
            val response: Response<UserMe> = callSync.execute()
            model.user = response.body()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        val menusearch = menu!!.findItem(R.id.menu_search).actionView as SearchView
        menusearch.queryHint = resources.getString(R.string.search)
        menusearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String?): Boolean {
                // later
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val bunlde = Bundle()
                bunlde.putString("query", query)
                val joinClassFragment = JoinClassFragment()
                joinClassFragment.arguments = bunlde
                actionBar?.setLogo(R.drawable.ic_baseline_add_24)
                actionBar?.title = "Tham gia lớp học"
                return false
            }
        })
        return true
    }

    private fun setUpNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navcontroller = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navcontroller.graph, drawerLayout)
        nav_view.setupWithNavController(navcontroller)
        navcontroller.addOnDestinationChangedListener { _, destination, _ ->
            val actionBar: ActionBar? = supportActionBar
            actionBar?.setDisplayShowHomeEnabled(true)
            actionBar?.setDisplayUseLogoEnabled(true)
            if (destination.id == R.id.nav_home) {
                actionBar?.setLogo(R.drawable.ic_baseline_home_24)
                actionBar?.title = "Trang chủ"
            }
            if (destination.id == R.id.nav_joinclass) {
                actionBar?.setLogo(R.drawable.ic_baseline_add_24)
                actionBar?.title = "Tham gia lớp học"
            }
            if (destination.id == R.id.nav_myClass) {
                actionBar?.setLogo(R.drawable.ic_baseline_class_24)
                actionBar?.title = "Lớp học của tôi"
            }
            if (destination.id == R.id.nav_createclass) {
                actionBar?.setLogo(R.drawable.ic_baseline_add_24)
                actionBar?.title = "Tạo lớp học"
            }
            if (destination.id == R.id.nav_profile) {
                actionBar?.setLogo(R.drawable.ic_baseline_profile_ind_24)
                actionBar?.title = "Thông tin người dùng"
            }
            if (destination.id == R.id.nav_setting) {
                actionBar?.setLogo(R.drawable.ic_baseline_settings_24)
                actionBar?.title = "Cài đặt"
            }
            if (destination.id == R.id.nav_logout) {
                val sharedprefernces = getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
                val edit = sharedprefernces.edit()
                edit.apply { remove("token") }.apply()
                val intent = Intent(this, LoginRegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
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

                item?.let { viewModel.selectItem(it) }
                Log.d("onActivityResult", viewModel.selectedItem.value.toString())
            }


        }
    }

    override fun toClass(classroom: Classroom) {
        val bundle = Bundle()
        //Log.d("MainActivity", "classroom $classroom")
        bundle.putSerializable("targetClassroom", classroom)
        //startActivity(Intent(this, ClassActivity::class.java).apply { putExtras(bundle) })
        navcontroller.navigate(R.id.action_nav_myClass_to_classActivity, bundle)
    }

}
