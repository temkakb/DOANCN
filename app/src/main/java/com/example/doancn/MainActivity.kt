package com.example.doancn
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.doancn.Fragments.CreateClass.CreateClassFragment
import com.example.doancn.Fragments.Home.HomeFragment
import com.example.doancn.Fragments.JoinClass.JoinClassFragment
import com.example.doancn.Fragments.MyClass.MyClassFragment
import com.example.doancn.Fragments.Profile.ProfileFragment
import com.example.doancn.Fragments.Setting.SettingFragment
import com.example.doancn.Models.UserMe
import com.example.doancn.Repository.AuthRepository
import com.example.doancn.Retrofit.RetrofitManager
import com.example.doancn.Utilities.JwtManager
import com.example.doancn.ViewModels.UserViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{

    private val FRAGMENT_HOME:Int = 0
    private val FRAGMENT_MYCLASS:Int = 1
    private val FRAGMENT_JOINCLASS:Int = 2
    private val FRAGMENT_SETTING:Int = 3
    private val FRAGMENT_PROFILE:Int = 4
    private val FRAGMENT_CREATECLASS:Int = 5

    var mCurrentFragment:Int = FRAGMENT_HOME


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Action bar
        setSupportActionBar(mToolbar)
        val toogle = ActionBarDrawerToggle(this,drawerLayout,mToolbar,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
        // Navigation drawer
        nav_view.setNavigationItemSelectedListener(this)
        mCurrentFragment = FRAGMENT_HOME
        replaceFragment(HomeFragment())
        nav_view.menu.findItem(R.id.nav_home).setChecked(true)
        // khai báo UserViewModel
        var model : UserViewModel =  ViewModelProvider(this)[UserViewModel::class.java]

        val sharedprefernces = getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
        val token : String? = sharedprefernces.getString("token",null)
        val intent = Intent(this,LoginRegisterActivity::class.java)
        if (token==null){ // CODE CHAY CHO MAU, co gi chu improve nha
            startActivity(intent)
            finish()
        }
        else {
            GlobalScope.launch {
                try {
                    val auth = AuthRepository()
                    val map = HashMap<String,String>()
                    map.put("token",token)
                    Log.i("MyToken",token)
                    auth.validate(map)
                    getMyUser(token,model)
                    if(model.user != null)
                    {
                        Log.i("Username",model.user!!.name)
                        runOnUiThread {
                            val header: View = nav_view.getHeaderView(0)
                            header.user_name.text = model.user!!.name
                            header.user_email.text = model.user!!.account.email
                            if(model.user!!.image != null){
                                val imgDecode: ByteArray = Base64.getDecoder().decode(model.user!!.image)
                                val bmp = BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
                                header.user_image.setImageBitmap(bmp)
                            }
                            else
                            {
                                when(model.user!!.gender.genderID)
                                {
                                    1 -> { header.user_image.setImageResource(R.drawable.man) }
                                    2 -> { header.user_image.setImageResource(R.drawable.femal) }
                                    3 -> { header.user_image.setImageResource(R.drawable.orther) }
                                }
                            }
                        }
                    }

                    JwtManager.apply {
                        getpublickey(token)
                        readrolefromtokenJws()
                        if (role == "STUDENT"){
                            runOnUiThread {
                                val nav_Menu: Menu = nav_view.menu
                                nav_Menu.findItem(R.id.nav_createclass).isVisible = false
                            }
                        }
                        else if(role == "TEACHER") {
                            runOnUiThread {
                                val nav_Menu: Menu = nav_view.menu
                                nav_Menu.findItem(R.id.nav_joinclass).isVisible = false
                            }
                        }
                    }
                }
                // token ko hop le
                catch (e: retrofit2.HttpException)
                {
                    sharedprefernces.edit().clear().apply()
                    startActivity(intent)
                    finish()
                }
            }
        }
        /***-----------------xem co token duoi sharedpre pho` ran ko. neu co thi validate thu-------------------- ***/

        val actionBar : ActionBar? = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayUseLogoEnabled(true)
        actionBar?.setLogo(R.drawable.ic_baseline_home_24)
        actionBar?.title = "Home"

    }


    private fun getMyUser (token: String, model: UserViewModel){

        val callSync : Call<UserMe> = RetrofitManager.userapi.getme("Bearer "+token)
        try {
            val response:Response<UserMe> = callSync.execute()
            model.user = response.body()
        }catch ( ex : Exception)
        {
            ex.printStackTrace()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        val menusearch = menu!!.findItem(R.id.menu_search).actionView as SearchView
        menusearch.queryHint= resources.getString(R.string.search)
        menusearch.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                // later
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val bunlde = Bundle()
                bunlde.putString("query",query)
                val joinClassFragment = JoinClassFragment()
                joinClassFragment.arguments=bunlde
                actionBar?.setLogo(R.drawable.ic_baseline_add_24)
                actionBar?.title = "Tham gia lớp học"
                replaceFragment(joinClassFragment)
                mCurrentFragment = FRAGMENT_JOINCLASS
                return false
            }
        })
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val actionBar : ActionBar? = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayUseLogoEnabled(true)
        val id : Int = item.itemId
        when(id){
            R.id.nav_home -> {
                if(mCurrentFragment != FRAGMENT_HOME){
                    replaceFragment( HomeFragment())
                    actionBar?.setLogo(R.drawable.ic_baseline_home_24)
                    actionBar?.title = "Home"
                    mCurrentFragment = FRAGMENT_HOME
                }
            }
            R.id.nav_myClass -> {
                if(mCurrentFragment != FRAGMENT_MYCLASS){
                    actionBar?.setLogo(R.drawable.ic_baseline_class_24)
                    actionBar?.title = "Lớp học của tôi"
                    replaceFragment( MyClassFragment())
                    mCurrentFragment = FRAGMENT_MYCLASS
                }
            }
            R.id.nav_joinclass -> {
                if(mCurrentFragment != FRAGMENT_JOINCLASS){
                    actionBar?.setLogo(R.drawable.ic_baseline_add_24)
                    actionBar?.title = "Tham gia lớp học"
                    replaceFragment( JoinClassFragment())
                    mCurrentFragment = FRAGMENT_JOINCLASS
                }
            }
            R.id.nav_createclass -> {
                if(mCurrentFragment != FRAGMENT_CREATECLASS){
                    actionBar?.setLogo(R.drawable.ic_baseline_add_24)
                    actionBar?.setTitle("Tạo lớp học")
                    replaceFragment( CreateClassFragment())
                    mCurrentFragment = FRAGMENT_CREATECLASS
                }
            }
            R.id.nav_setting -> {
                if(mCurrentFragment != FRAGMENT_SETTING){
                    actionBar?.setLogo(R.drawable.ic_baseline_settings_24)
                    actionBar?.setTitle("Cài đặt")
                    replaceFragment( SettingFragment())
                    mCurrentFragment = FRAGMENT_SETTING
                }
            }
            R.id.nav_profile -> {
                if(mCurrentFragment != FRAGMENT_PROFILE) {
                    actionBar?.setLogo(R.drawable.ic_baseline_profile_ind_24)
                    actionBar?.setTitle("Thông tin người dùng")
                    replaceFragment(ProfileFragment())
                    mCurrentFragment = FRAGMENT_PROFILE
                }
            }
            R.id.nav_logout -> {
                val sharedprefernces = getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
                val edit = sharedprefernces.edit()
                edit.apply { remove("token") }.apply()
                val intent = Intent(this,LoginRegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun replaceFragment(fragment: Fragment){
        val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame,fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

}
