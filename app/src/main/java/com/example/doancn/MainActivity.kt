package com.example.doancn

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.doancn.DI.DataState
import com.example.doancn.Fragments.CreateClass.CreateClassViewModel
import com.example.doancn.Fragments.JoinClass.JoinClassViewModel
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.UserMe
import com.example.doancn.Models.classModel.ClassQuest
import com.example.doancn.Retrofit.RetrofitManager
import com.example.doancn.ViewModels.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import retrofit2.Response
import java.util.*
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), IMainActivity {

    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private lateinit var navcontroller: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    private val createClassViewModel: CreateClassViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val joinClassViewModel: JoinClassViewModel by viewModels()

    @Inject
    lateinit var sharedPrefernces: SharedPreferences

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
        //deep link
        val data: Uri? = intent?.data
        data.let {
            Log.d("tokendeeplink", it.toString())
        }

        //Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("108237023578-64dd4m9o65bhbb4vmbqlth1r8s53uo7u.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // khai b??o UserViewModel
        val model: UserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        if (mainViewModel.token == null) { // CODE CHAY CHO MAU, co gi chu improve nha
            toLoginFragment()
        } else {
            // obverse data
            obverseData()
            mainViewModel.doValidateToken()
            GlobalScope.launch {

                getMyUser(mainViewModel.token!!, model)
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

                withContext(Dispatchers.Main) {
                    Log.d("MainActivity", mainViewModel.role!!)
                    if (mainViewModel.role == "STUDENT") {
                        runOnUiThread {
                            val navmenu: Menu = nav_view.menu
                            navmenu.findItem(R.id.nav_createclass).isVisible = false
                            navmenu.findItem(R.id.nav_joinclass).isVisible = true
                        }
                    } else if (mainViewModel.role == "TEACHER") {
                        runOnUiThread {
                            val navmenu: Menu = nav_view.menu
                            navmenu.findItem(R.id.nav_joinclass).isVisible = false
                            navmenu.findItem(R.id.nav_createclass).isVisible = true
                        }
                    }

                }

            }
        }
        /***-----------------xem co token duoi sharedpre pho` ran ko. neu co thi validate thu-------------------- ***/

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayUseLogoEnabled(true)
        actionBar?.setLogo(R.drawable.ic_baseline_home_24)
        actionBar?.title = "Trang ch???"
        intent.getBooleanExtra("backToListClass", false).let {
            if (it) {
                navcontroller.navigate(R.id.action_nav_home_to_myClassRoomFragment)
            }
        }
    }


    override fun onResume() {

        super.onResume()
    }

    private suspend fun getMyUser(token: String, model: UserViewModel) {
            val response: Response<UserMe> = RetrofitManager.userapi.getme(token)
            if (response.isSuccessful)
            model.user = response.body()
            else
                Log.i("L???i",response.errorBody().toString())

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
                if (!query.isNullOrEmpty()) {
                    val bundle = Bundle()
                    bundle.putString("searchkey", query)
                    navcontroller.navigateUp()
                    navcontroller.navigate(R.id.action_nav_home_to_nav_joinclass, bundle)

                } else {
                    Log.d("yepyepyep", "eweewew")
                    Toast.makeText(this@MainActivity, "B???n ch??a nh???p g?? c???", Toast.LENGTH_SHORT)
                        .show()
                }

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
                actionBar?.title = "Trang ch???"
            }
            if (destination.id == R.id.nav_joinclass) {
                actionBar?.setLogo(R.drawable.ic_baseline_add_24)
                actionBar?.title = "Tham gia l???p h???c"
            }
            if (destination.id == R.id.nav_myClass) {
                actionBar?.setLogo(R.drawable.ic_baseline_class_24)
                actionBar?.title = "L???p h???c c???a t??i"
            }
            if (destination.id == R.id.nav_createclass) {
                actionBar?.setLogo(R.drawable.ic_baseline_add_24)
                actionBar?.title = "T???o l???p h???c"
            }
            if (destination.id == R.id.nav_profile) {
                actionBar?.setLogo(R.drawable.ic_baseline_profile_ind_24)
                actionBar?.title = "Th??ng tin ng?????i d??ng"
            }
            if (destination.id == R.id.nav_setting) {
                actionBar?.setLogo(R.drawable.ic_baseline_settings_24)
                actionBar?.title = "C??i ?????t"
            }
            if (destination.id == R.id.nav_logout) {
                sharedPrefernces.edit().clear().commit()
                mGoogleSignInClient.signOut()
                toLoginFragment()
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
            val geocoder = Geocoder(this,  Locale.getDefault())
            latitude?.let { longitude?.let { it1 -> geocoder.getFromLocation(it, it1, 1) } }?.apply {
                if(this.isNotEmpty()){
                    val item = this?.get(0)?.let {
                        ClassQuest.Location(
                            city = if(it.locality==null) it.subAdminArea else it.locality,
                            coordinateX = latitude!!,
                            coordinateY = longitude!!,
                            address = ""
                        )
                    }

                    Log.d("TAG", item.toString())

                    item?.let { createClassViewModel.selectItem(it) }
                    Log.d("onActivityResult", createClassViewModel.selectedItem.value.toString())
                }

            }


        }
    }

    override fun toClass(classroom: Classroom) {
        val bundle = Bundle()
        bundle.putSerializable("targetClassroom", classroom)
        navcontroller.navigate(R.id.action_nav_myClass_to_classActivity, bundle)
    }


    fun homeToClass(classroom: Classroom) {
        val bundle = Bundle()
        bundle.putSerializable("targetClassroom", classroom)
        startActivity(Intent(this, ClassActivity::class.java).apply { putExtras(bundle) })
    }


    private fun obverseData() { // check token valid
        lifecycleScope.launchWhenCreated {
            mainViewModel.validatetoken.collect { datastate ->
                when (datastate) {
                    is DataState.Success -> Toast.makeText(
                        this@MainActivity,
                        datastate.data,
                        Toast.LENGTH_SHORT
                    ).show()
                    is DataState.Error -> {
                        Toast.makeText(this@MainActivity, datastate.data, Toast.LENGTH_SHORT).show()

                        sharedPrefernces.edit().clear().commit()
                        toLoginFragment()
                    }
                }
            }
        }


    }

    private fun toLoginFragment() {
        val intent = Intent(this, LoginRegisterActivity::class.java)
        startActivity(intent)
        finish()

    }


}
