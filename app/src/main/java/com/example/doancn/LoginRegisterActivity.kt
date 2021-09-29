package com.example.doancn

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.doancn.Adapters.ViewpageLoginSigupAdapter
import kotlinx.android.synthetic.main.login_register_container.*


class LoginRegisterActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        setContentView(R.layout.login_register_container)
        super.onCreate(savedInstanceState)
        frag_container.adapter= ViewpageLoginSigupAdapter(supportFragmentManager)
        tab_login_sigup.setupWithViewPager(frag_container)
        container.setOnClickListener {view ->
            val inputmethodmanager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputmethodmanager.hideSoftInputFromWindow(view.windowToken,0)
        }

    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            setheighpercent()
        }
        if  (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            setheighpercent()
        }
    }
    inline fun setheighpercent (){
        val constrainlayout = findViewById(R.id.container) as ConstraintLayout
        val set = ConstraintSet()
        set.clone(constrainlayout)
        set.constrainPercentHeight(R.id.constraintlayout2,0.9.toFloat())
        set.applyTo(constrainlayout)

    }
}
