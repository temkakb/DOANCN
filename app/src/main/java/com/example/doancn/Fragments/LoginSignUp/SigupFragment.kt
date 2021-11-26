package com.example.doancn.Fragments.LoginSignUp

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.doancn.DI.DataState
import com.example.doancn.Models.AccountSignUp
import com.example.doancn.R
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.signup_fragment.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SigupFragment : Fragment() {
    @Inject
    lateinit var accountSignUp: AccountSignUp
    private val viewModel: SignUpViewModel by viewModels()
    lateinit private var btnnext: Button
    lateinit private var btnprevious: Button
    lateinit var txtfinish: TextView
    lateinit var process: ProgressBar
    var genderpick: HashMap<String, String> = HashMap()
    lateinit var genderarray: Array<String>
    lateinit var genderpost: Array<String>
    val listfragment = listOf(
        UserFillInfoFragment(),
        UserFillInfoFragment2(),
        UserFillInfoFragment3(),
    )

    private var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        genderarray = requireContext().resources.getStringArray(R.array.gender_select)
        genderpost = requireContext().resources.getStringArray(R.array.gender_post)
        var postion = 0
        genderarray.forEach {
            genderpick[it] = genderpost[postion++] // Nữ=FEMALE, Nam=MALE, Khác=OTHER
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.signup_fragment, container, false)
        btnnext = view.btn_next
        btnprevious = view.btn_previous
        process = view.process
        txtfinish = view.txt_finish
        // get view model
        btnprevious.visibility = View.GONE
        observeData()
        dotranscation(YouAreFragment()) // transcation a child fragment
        setEvent2Button() // set event cho 2 nut
        // set event
        return view
    }

    private fun setEvent2Button() {

            btnnext.setOnClickListener {

                btnprevious.visibility = View.VISIBLE // hien thi nut back
                val fragment: Fragment? =
                    childFragmentManager.findFragmentById(R.id.sigup_container)
                if (setdata(fragment!!)) {
                    if (fragment::class != UserFillInfoFragment3::class) { // check fragment end then do stuff
                        dotranscation(listfragment[position])


                        position++
                    } else {
                        viewModel.doSigup()

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

    private fun setdata(fragment: Fragment): Boolean { // code no clear but it work , no time to óp ti mize
        if (fragment::class == UserFillInfoFragment::class) { // in fragment 1
            val email = fragment.requireView().findViewById(R.id.email) as TextInputEditText
            val password = fragment.requireView().findViewById(R.id.password) as TextInputEditText
            val name = fragment.requireView().findViewById(R.id.yourname) as TextInputEditText
            if (isEmptyText(email.text.toString()) || isEmptyText(password.text.toString()) || isEmptyText(
                    name.text.toString()
                )
            ) {
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
            if (!validateemailaddress(email.text.toString())) {
                Toast.makeText(requireContext(), "Email sai định dạng", Toast.LENGTH_SHORT).show()
                return false
            }
            accountSignUp.password = password.text.toString()
            accountSignUp.user.name = name.text.toString()
            accountSignUp.email = email.text.toString()

        }
        if (fragment::class == UserFillInfoFragment2::class) {
            val gender = fragment.requireView().findViewById(R.id.gender) as AutoCompleteTextView
            val phone = fragment.requireView().findViewById(R.id.phone) as TextInputEditText
            val address = fragment.requireView().findViewById(R.id.address) as TextInputEditText
            if (!phone.text.isNullOrEmpty()) {
                if (!validatePhoneNumber(phone.text.toString())) {
                    Toast.makeText(
                        requireContext(),
                        "Số điện thoại không được chứa chữ hoặc ký tự đặc biệt",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                if (phone.text!!.length < 9) {
                    Toast.makeText(
                        requireContext(),
                        "Số điện thoại không hợp lệ (dưới 9 số)",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }

            }
            try {
                accountSignUp.mgender = genderpick.get(gender.text.toString())!!
            } catch (e: NullPointerException) {
                accountSignUp.mgender = genderpick.get("Khác")!!
            }
            accountSignUp.user.phoneNumber = phone.text.toString()
            accountSignUp.user.address = address.text.toString()
        }
        if (fragment::class == UserFillInfoFragment3::class) {
            val currentWorkPlace =
                fragment.requireView().findViewById(R.id.currentWorkPlace) as TextInputEditText
            val educationLevel =
                fragment.requireView().findViewById(R.id.educationLevel) as TextInputEditText
            val dateborn = fragment.requireView().findViewById(R.id.dateborn) as TextInputEditText
            if (isEmptyText(dateborn.text.toString())) {
                Toast.makeText(requireContext(), "Chưa chọn ngày sinh", Toast.LENGTH_SHORT).show()
                return false
            }

            accountSignUp.user.currentWorkPlace = currentWorkPlace.text.toString()
            accountSignUp.user.educationLevel = educationLevel.text.toString()
            accountSignUp.user.dob = dateborn.text.toString()
        }
        return true
    }


    private fun dotranscation(fragment: Fragment) {
        val transcation = childFragmentManager.beginTransaction()
        transcation.replace(R.id.sigup_container, fragment)
        transcation.addToBackStack(null)
        transcation.commit()
    }

    private fun observeData() {
        lifecycleScope.launchWhenStarted {
            viewModel.signupstatus.collect {
                when (it) {
                    is DataState.Loading -> {
                        process.visibility = View.VISIBLE
                        btnnext.text = null
                    }
                    is DataState.Success -> {
                        dotranscation(ValidateEmailFragment())
                        process.visibility = View.INVISIBLE
                        txtfinish.visibility = View.VISIBLE
                        btnprevious.visibility = View.GONE
                        btnnext.visibility = View.GONE
                    }
                    is DataState.Error -> {
                        process.visibility = View.INVISIBLE
                        btnnext.text = resources.getString(R.string.next2)
                        Toast.makeText(context, it.data, Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    companion object {
        fun isEmptyText(text: String): Boolean {
            return if (text.trim { it <= ' ' }.length > 0) false else true
        }

        fun validateemailaddress(email: String): Boolean {

            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun validatePhoneNumber(phonenumber: String): Boolean {
            if (phonenumber.isDigitsOnly())
                return true
            return false
        }
    }

// nỗi nhục thấu trời xanh
}