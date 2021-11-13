package com.example.doancn.Fragments.LoginSignUp


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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
import kotlinx.android.synthetic.main.enter_code_verify_dialog.*
import kotlinx.android.synthetic.main.forgotpassword_dialog.*
import kotlinx.android.synthetic.main.forgotpassword_dialog.btn_go
import kotlinx.android.synthetic.main.forgotpassword_dialog.process
import kotlinx.android.synthetic.main.login_fragment.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var process: ProgressBar
    lateinit var btnlogin: Button
    lateinit var forgotpassword: TextView
    lateinit var email: String
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.login_fragment, container, false)
        btnlogin = view.btnlogin
        process = view.process
        forgotpassword = view.txt_quenmk
        setEventButtonLogin(view)
        seteventtextchange(view)
        return view
    }

    override fun onResume() {
        observeData()
        super.onResume()
    }

    override fun onAttach(context: Context) {
        Log.d("contextne", context.toString())
        super.onAttach(context)
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
        forgotpassword.setOnClickListener {

            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.forgotpassword_dialog)
            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            forgotPasswordObserveData(dialog)
            dialog.show()
            dialog.btn_go.setOnClickListener {
                viewModel.email = dialog.email.text.toString()
                viewModel.requestForgotPassword(viewModel.email!!)

            }
            dialog.cancel_button.setOnClickListener {
                dialog.dismiss()
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

    private fun forgotPasswordObserveData(dialog: Dialog) {
        lifecycleScope.launchWhenCreated {
            viewModel.forgotPasswordStatus.collect { it ->
                when (it) {
                    is DataState.Loading -> {
                        dialog.process.visibility = View.VISIBLE
                        dialog.btn_go.text = null
                    }
                    is DataState.Error -> {
                        dialog.process.visibility = View.GONE
                        dialog.btn_go.text = resources.getString(R.string.confirm)
                        Toast.makeText(
                            requireContext(),
                            it.data,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is DataState.Success -> {
                        dialog.setContentView(R.layout.enter_code_verify_dialog)
                        dialog.btn_go.setOnClickListener {
                            viewModel.validateCode(dialog.code.text.toString())
                        }
                        validatePasswordObserveData(dialog)
                        val countDownTimer = object : CountDownTimer(60000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val timme =
                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                                dialog.btn_countdown.text = timme.toString()
                            }

                            override fun onFinish() {
                                dialog.btn_countdown.setIconResource(R.drawable.ic_baseline_settings_backup_restore_5000)
                                dialog.btn_countdown.isCheckable = true
                                dialog.btn_countdown.setOnClickListener {
                                    viewModel.requestForgotPassword(viewModel.email!!)
                                }
                            }
                        }
                        countDownTimer.start()
                    }
                }
            }
        }
    }

    private fun validatePasswordObserveData(dialog: Dialog) {
        lifecycleScope.launchWhenCreated {
            viewModel.validateCode.collect {
                when (it) {
                    is DataState.Loading -> {
                        dialog.process.visibility = View.VISIBLE
                        dialog.btn_go.text = null
                    }
                    is DataState.Error -> {
                        dialog.process.visibility = View.GONE
                        dialog.btn_go.text = resources.getString(R.string.confirm)
                        Toast.makeText(
                            requireContext(),
                            it.data,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is DataState.Success -> {
                        dialog.dismiss()
                        val dialogalert = AlertDialog.Builder(context)
                        dialogalert.setTitle(resources.getString(R.string.sended_password_title))
                            .setMessage(resources.getString(R.string.sended_password_body))
                            .setPositiveButton(resources.getString(R.string.confirm)) { dialoginterface, int ->
                                dialoginterface.dismiss()
                            }
                        dialogalert.show()

                    }
                }
            }
        }

    }


}


