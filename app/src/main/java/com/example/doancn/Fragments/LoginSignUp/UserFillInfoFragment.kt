package com.example.doancn.Fragments.LoginSignUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.doancn.R
import kotlinx.android.synthetic.main.login_fragment.view.*
import kotlinx.android.synthetic.main.user_fill_infomation_fragment.view.*

open class UserFillInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.user_fill_infomation_fragment, container, false)

        setEventOnTextChange(view)
        return view
    }

    private fun setEventOnTextChange(view: View) {
        view.email.doOnTextChanged { text, start, before, count ->
            if (!SigupFragment.validateemailaddress(text.toString()))
                view.email.error = "Email sai định dạng"
            else
                view.email.error = null
        }
        view.password.doOnTextChanged { text, start, before, count ->
            if(text!!.length<8) {view.password.error="Mật khẩu phải lớn hơn 8 ký tự"
                view.signup_textinputlayout2.isEndIconVisible=false }
            else{
                view.password.error=null
                view.signup_textinputlayout2.isEndIconVisible=true
            } }
    }
}