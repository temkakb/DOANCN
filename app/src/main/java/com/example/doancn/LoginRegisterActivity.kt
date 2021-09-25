package com.example.doancn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.doancn.Adapters.ViewpageLoginSigupAdapter
import kotlinx.android.synthetic.main.login_register_container.*


class LoginRegisterActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        setContentView(R.layout.login_register_container)
        super.onCreate(savedInstanceState)
        frag_container.adapter= ViewpageLoginSigupAdapter(supportFragmentManager)
        tab_login_sigup.setupWithViewPager(frag_container)

    }
}
//comment gi do
//giii gii do
// gi gi do