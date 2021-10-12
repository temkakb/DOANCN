package com.example.doancn.Fragments.LoginSignUp

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.doancn.LoginRegisterActivity
import com.example.doancn.Models.AccountSignUp
import com.example.doancn.Models.User
import com.example.doancn.R
import com.example.doancn.Repository.AuthRepository
import com.example.doancn.ViewModels.MviewmodelProviderFactory
import com.example.doancn.ViewModels.SignUpManagerViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.signup_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.EOFException
import java.net.SocketTimeoutException


class SigupFragment : Fragment() {
    lateinit private var viewModel: SignUpManagerViewModel
    lateinit private var btnnext: Button
    lateinit private var btnprevious: Button
    private var position = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = generateviewmodel() // when create fragement dong thoi create viewmodel
        val view = layoutInflater.inflate(R.layout.signup_fragment, container, false)
        btnnext = view.btn_next
        btnprevious = view.btn_previous
        // get view model
        btnprevious.visibility = View.GONE
        dotranscation(youaresingleton) // transcation a child fragment
        setEvent2Button() // set event cho 2 nut
        // set event
        return view
    }

    private fun setEvent2Button() {
        GlobalScope.launch(Dispatchers.Default) {
            btnnext.setOnClickListener {
                btnprevious.visibility = View.VISIBLE // hien thi nut back
                val fragment: Fragment? =
                    childFragmentManager.findFragmentById(R.id.sigup_container)
                if (setdata(fragment!!)) {
                    if (fragment::class != UserFillInfoFragment3::class) { // check fragment end then do stuff
                        dotranscation(managersingleton.listfragment[position])
                        // set event khi fragment change
                        position++
                    } else {
                        Log.d("gigidone",viewModel.account.user.name)
                        Log.d("gigidone",viewModel.account.user.address)
                        Log.d("gigidone",viewModel.account.mgender)
                        Log.d("gigidone",viewModel.account.password)
                        Log.d("gigidone",viewModel.account.user.currentWorkPlace)
                        GlobalScope.launch {
                            try {
                                val authapi = AuthRepository()

                                authapi.signup(viewModel.account) // post dang ky

                                dotranscation(managersingleton.listfragment[3]) // fragment hoan tat
                                withContext(Dispatchers.Main)
                                {
                                    btnprevious.visibility = View.GONE
                                    btnnext.visibility = View.GONE
                                }
                            } catch (e: HttpException) {
                                withContext(Dispatchers.Main) {
                                    val jObjError = JSONObject(e.response()?.errorBody()!!.string())
                                    val msg = jObjError.get("message")
                                    Toast.makeText(
                                        requireContext(),
                                        msg.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (Eof: EOFException) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Thao tác quá nhanh, từ từ thôi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: SocketTimeoutException) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Lỗi mạng",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
            btnprevious.setOnClickListener {
                position--
                childFragmentManager.popBackStack()
                if (childFragmentManager.backStackEntryCount < 3) {
                    btnprevious.visibility = View.GONE
                }
            }
        }
    }

    private fun setdata(fragment: Fragment): Boolean {
        if (fragment::class == UserFillInfoFragment::class) { // in fragment 1
            val email = fragment.requireView().findViewById(R.id.email) as TextInputEditText
            val password = fragment.requireView().findViewById(R.id.password) as TextInputEditText
            val name = fragment.requireView().findViewById(R.id.yourname) as TextInputEditText
            if (isEmptyET(email) || isEmptyET(password) || isEmptyET(name)) {
                Toast.makeText(
                    requireContext(),
                    "Các ô Bắt buộc* không được trống",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            if (password.text!!.length < 8) {
                Toast.makeText(requireContext(), "Mật khẩu dưới 8 ký tự", Toast.LENGTH_SHORT).show()
                return false
            }
            if (!validateemailaddress(email)) {
                Toast.makeText(requireContext(), "Email sai định dạng", Toast.LENGTH_SHORT).show()
                return false
            }
            viewModel.account.password = password.text.toString()
            viewModel.account.user.name = name.text.toString()
            viewModel.account.email = email.text.toString()

        }
        if (fragment::class == UserFillInfoFragment2::class) {
            val gender = fragment.requireView().findViewById(R.id.gender) as AutoCompleteTextView
            val phone = fragment.requireView().findViewById(R.id.phone) as TextInputEditText
            val address = fragment.requireView().findViewById(R.id.address) as TextInputEditText
            try {
                viewModel.account.mgender = viewModel.genderpick.get(gender.text.toString())!!
            } catch (e: NullPointerException) {
                viewModel.account.mgender = viewModel.genderpick.get("Khác")!!
            }
            viewModel.account.user.phoneNumber = phone.text.toString()
            viewModel.account.user.address = address.text.toString()
        }
        if (fragment::class == UserFillInfoFragment3::class) {
            val currentWorkPlace =
                fragment.requireView().findViewById(R.id.currentWorkPlace) as TextInputEditText
            val educationLevel =
                fragment.requireView().findViewById(R.id.educationLevel) as TextInputEditText
            val dateborn = fragment.requireView().findViewById(R.id.dateborn) as TextInputEditText
            if (isEmptyET(dateborn)) return false

            viewModel.account.user.currentWorkPlace = currentWorkPlace.text.toString()
            viewModel.account.user.educationLevel = educationLevel.text.toString()
            viewModel.account.user.dob = dateborn.text.toString()
        }
        return true
    }

    private fun generateviewmodel(): SignUpManagerViewModel {
        // remember view model tồn tại chung với activity . loginsignup activity
        val viewmodelfactory = MviewmodelProviderFactory(AccountSignUp(User()), requireContext())
        return ViewModelProvider(
            requireActivity() as LoginRegisterActivity,
            viewmodelfactory
        ).get(SignUpManagerViewModel::class.java)  // provide viewmodel cho toi!!
    }

    private fun dotranscation(fragment: Fragment) {
        val transcation = childFragmentManager.beginTransaction()
        transcation.replace(R.id.sigup_container, fragment)
        transcation.addToBackStack(null)
        transcation.commit()
    }

    private fun isEmptyET(etText: TextInputEditText): Boolean {
        return if (etText.text.toString().trim { it <= ' ' }.length > 0) false else true
    }

    private fun validateemailaddress(et: TextInputEditText): Boolean {
        val etstring = et.text.toString()
        return Patterns.EMAIL_ADDRESS.matcher(etstring).matches()

    }

    object managersingleton { // tuan tu fragment
        var listfragment: List<Fragment>

        init {
            listfragment = listOf<Fragment>(
                UserFillInfoFragment(),
                UserFillInfoFragment2(),
                UserFillInfoFragment3(),
                ValidateEmailFragment()
            )
        }
    }

    object youaresingleton : YouAreFragment() // hang dat biet =)) con ghe ko cho vao hang
}