package com.example.doancn.Fragments.LoginSignUp


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.doancn.MainActivity
import com.example.doancn.Models.Account
import com.example.doancn.R
import com.example.doancn.Repository.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject







class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.login_fragment,container,false)
        val btnlogin = view.findViewById(R.id.btnlogin) as Button
        btnlogin.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val email = view.findViewById(R.id.email_login) as TextInputEditText
                    val password = view.findViewById(R.id.password_login) as TextInputEditText
                    val repository = AuthRepository()
                    val account = Account(email.text.toString(),password.text.toString())
                    val map = repository.login(account)
                    val sharedPreferences = requireContext().getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.apply{
                        putString("token",map.get("token"))
                    }.apply()
                    val intent = Intent(context,MainActivity::class.java)
                    requireContext().startActivity(intent)
                    requireActivity().finish()
                }
                catch (e: retrofit2.HttpException)
                {
                    val jObjError = JSONObject(e.response()?.errorBody()?.string())
                    val mssg=jObjError.get("message")
                    withContext(Dispatchers.Main) {
                    Toast.makeText(context, "$mssg", Toast.LENGTH_SHORT).show()
                }
                }
            }
        }
        return view
    }
}