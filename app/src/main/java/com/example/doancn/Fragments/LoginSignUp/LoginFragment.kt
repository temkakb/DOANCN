package com.example.doancn.Fragments.LoginSignUp


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.doancn.DI.DataState
import com.example.doancn.Fragments.LoginSignUp.SigupFragment.Companion.isEmptyText
import com.example.doancn.Fragments.LoginSignUp.SigupFragment.Companion.validateemailaddress
import com.example.doancn.MainActivity
import com.example.doancn.Models.Account
import com.example.doancn.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.login_fragment.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var process: ProgressBar
    lateinit var btnlogin: Button
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.login_fragment, container, false)
        btnlogin = view.btnlogin
        process = view.process
        setEventButtonLogin(view)
        seteventtextchange(view)
        observeData()
        return view
    }

    private fun setEventButtonLogin(view: View) {
        btnlogin.setOnClickListener {
            val email = view.email_login
            val password = view.password_login
            when (caselogin(email.text.toString(), password.text.toString())) {
                0 -> Toast.makeText(context, "Chưa điền đủ thông tin", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(context, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(context, "Mật khẩu phải lớn hơn 8 ký tự", Toast.LENGTH_SHORT)
                    .show()
                -1 -> {
                    val account = Account(email.text.toString(), password.text.toString())
                    viewModel.doLogin(account = account)

                }
            }
        }
    }

    private fun caselogin(email: String, password: String): Int {
        if (  isEmptyText(email) ||isEmptyText(password))
            return 0
        if (!validateemailaddress(email))
            return 1
        if (password.length < 8)
            return 2
        return -1
    }

    private fun seteventtextchange(view: View) {
        view.email_login.doOnTextChanged { text, start, before, count ->
            if (!validateemailaddress(text.toString())) {
                view.email_login.error = "Email sai định dạng"
            } else view.email_login.error = null
        }
        view.password_login.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 8) {
                view.password_login.error = "Mật khẩu phải lớn hơn 8 ký tự"
                view.login_textinputlayout2.isEndIconVisible = false
            } else {
                view.password_login.error = null
                view.login_textinputlayout2.isEndIconVisible = true
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            viewModel.loginstatus.collect { event ->
                when (event) {
                    is DataState.Success -> {
                        val sharedPreferences = requireContext().getSharedPreferences(
                            "tokenstorage",
                            Context.MODE_PRIVATE
                        )
                        sharedPreferences.edit().apply {
                            putString("token", event.data!!.get("token"))
                        }.apply()
                        process.visibility = View.INVISIBLE
                        btnlogin.text = resources.getString(R.string.login)
                        val intent = Intent(context, MainActivity::class.java)
                        requireContext().startActivity(intent)
                        requireActivity().finish()

                    }
                    is DataState.Error -> {
                        process.visibility = View.INVISIBLE
                        btnlogin.text = resources.getString(R.string.login)
                        Toast.makeText(requireContext(), event.data, Toast.LENGTH_SHORT).show()
                    }
                    is DataState.Loading -> {

                        process.visibility = View.VISIBLE
                        btnlogin.text = "Đang đăng nhâp"
                    }

                }

            }
        }

    }


}


