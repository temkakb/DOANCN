package com.example.doancn

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.doancn.Fragments.CreateClass.CreateClassFragment
import com.example.doancn.Fragments.Home.HomeFragment
import com.example.doancn.Fragments.JoinClass.JoinClassFragment
import com.example.doancn.Fragments.MyClass.MyClassFragment
import com.example.doancn.Fragments.Profile.ProfileFragment
import com.example.doancn.Fragments.Setting.SettingFragment
import com.example.doancn.Models.User
import com.example.doancn.R.id.nav_home
import com.example.doancn.Repository.AuthRepository
import com.example.doancn.Utilities.JwtManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{

    private val FRAGMENT_HOME:Int = 0
    private val FRAGMENT_MYCLASS:Int = 1
    private val FRAGMENT_JOINCLASS:Int = 2
    private val FRAGMENT_CREATECLASS:Int = 3
    private val FRAGMENT_SETTING:Int = 4
    private val FRAGMENT_PROFILE:Int = 5
    private val FRAGMENT_SHARE:Int = 6
    private val FRAGMENT_RATEUS:Int = 7

    private var mCurrentFragment: Int = -1

    private lateinit var user : User



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState==null){
            mCurrentFragment = FRAGMENT_HOME
            replaceFragment(HomeFragment())
        }

        setSupportActionBar(mToolbar)
        val toogle = ActionBarDrawerToggle(this,drawerLayout,mToolbar,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.menu.findItem(nav_home).setChecked(true)

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
                    auth.validate(map)
                    JwtManager.apply {
                        getpublickey(token)
                        readrolefromtokenJws()
                        if (JwtManager.role == "STUDENT"){
                            runOnUiThread {
                                val nav_Menu: Menu = nav_view.getMenu()
                                nav_Menu.findItem(R.id.nav_createclass).setVisible(false)
                            }
                        }
                        else if(JwtManager.role == "TEACHER") {
                            runOnUiThread {
                                val nav_Menu: Menu = nav_view.getMenu()
                                nav_Menu.findItem(R.id.nav_joinclass).setVisible(false)
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



    }


    override fun onSaveInstanceState(outState: Bundle) {
        if (mCurrentFragment != -1) {
            outState.putInt("mCurrentFragment", mCurrentFragment)}
        super.onSaveInstanceState(outState)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id : Int = item.itemId
        if( id == nav_home){
            if(mCurrentFragment != FRAGMENT_HOME){
                replaceFragment( HomeFragment())
                mCurrentFragment = FRAGMENT_HOME
            }
        }else if (id == R.id.nav_myClass ){
            if(mCurrentFragment != FRAGMENT_MYCLASS){
                replaceFragment( MyClassFragment())
                mCurrentFragment = FRAGMENT_MYCLASS
            }
        }else if (id == R.id.nav_createclass ){
            if(mCurrentFragment != FRAGMENT_CREATECLASS){
                replaceFragment( CreateClassFragment())
                mCurrentFragment = FRAGMENT_CREATECLASS
            }
        }else if (id == R.id.nav_joinclass ){
            if(mCurrentFragment != FRAGMENT_JOINCLASS){
                replaceFragment( JoinClassFragment())
                mCurrentFragment = FRAGMENT_JOINCLASS
            }
        }else if (id == R.id.nav_setting ){
            if(mCurrentFragment != FRAGMENT_SETTING){
                replaceFragment( SettingFragment())
                mCurrentFragment = FRAGMENT_SETTING
            }
        }else if (id == R.id.nav_profile ){
            if(mCurrentFragment != FRAGMENT_PROFILE){
                replaceFragment( ProfileFragment())
                mCurrentFragment = FRAGMENT_PROFILE
            }
        }else if (id == R.id.nav_logout ){
            val sharedprefernces = getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
            val edit = sharedprefernces.edit()
            edit.apply { remove("token") }.apply()
            val intent = Intent(this,LoginRegisterActivity::class.java)
            startActivity(intent)
            finish()
        }else if (id == R.id.nav_share ){

        }else if (id == R.id.nav_rate_us ){

        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment){
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

