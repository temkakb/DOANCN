package com.example.doancn.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.doancn.Models.AccountSignUp
import com.example.doancn.Models.User
import com.example.doancn.R
import com.example.doancn.ViewModels.MviewmodelProviderFactory
import com.example.doancn.ViewModels.SignUpManagerViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.NullPointerException

class SigupFragment : Fragment() {
    private var position=0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewmodel=generateviewmodel() // when create fragement dong thoi create viewmodel
        val view = layoutInflater.inflate(R.layout.signup_fragment,container,false)
        val btnnext =view.findViewById(R.id.btn_next) as Button
        val btnprevious = view.findViewById(R.id.btn_previous) as Button
            // get view model
        btnprevious.visibility=View.GONE
        dotranscation(youaresingleton) // transcation a child fragment
        GlobalScope.launch {
            setevent(btnprevious,btnnext,viewmodel) // set event cho 2 nut

        }
        // set event
        return  view
    }
     private fun setevent(btnprevious: Button, btnnext: Button,viewmodel: SignUpManagerViewModel){
        GlobalScope.launch(Dispatchers.Default) {
            btnnext.setOnClickListener {
                btnprevious.visibility=View.VISIBLE // hien thi nut back
                val fragment : Fragment?=childFragmentManager.findFragmentById(R.id.sigup_container)
                if(fragment!!::class!=UserFillInfoFragment3::class){ // check fragment end then do stuff
                    dotranscation(managersingleton.listfragment[position])
                    seteventfragmentchange(fragment,viewmodel) // set event khi fragment change
                    position++
                }
                else
                {
                     // post man mai lam
                }
            }
            btnprevious.setOnClickListener {
                position--
                childFragmentManager.popBackStack()
                if(childFragmentManager.backStackEntryCount<3){
                    btnprevious.visibility=View.GONE
                }
            }
        }
    }
    private fun seteventfragmentchange(fragment: Fragment,viewModel: SignUpManagerViewModel){
        if(fragment::class==UserFillInfoFragment::class){
            val email = fragment.requireView().findViewById(R.id.email) as TextInputEditText
            val password=fragment.requireView().findViewById(R.id.password) as TextInputEditText
            val name=fragment.requireView().findViewById(R.id.yourname) as TextInputEditText
            viewModel.account.password=password.text.toString()
            viewModel.account.user.name=name.text.toString()
            viewModel.account.email=email.text.toString()

        }
        if(fragment::class==UserFillInfoFragment2::class){
            val gender = fragment.requireView().findViewById(R.id.gender) as AutoCompleteTextView
            val phone = fragment.requireView().findViewById(R.id.phone) as TextInputEditText
            val address=fragment.requireView().findViewById(R.id.address) as TextInputEditText
            viewModel.account.user.gender = viewModel.genderpick.get(gender.text.toString())
            viewModel.account.user.phoneNumber=phone.text.toString()
            viewModel.account.user.address=address.text.toString()

        }
        if(fragment::class==UserFillInfoFragment3::class){
            val currentWorkPlace = fragment.requireView().findViewById(R.id.currentWorkPlace) as TextInputEditText
            val educationLevel = fragment.requireView().findViewById(R.id.educationLevel) as TextInputEditText
            val dateborn = fragment.requireView().findViewById(R.id.dateborn) as TextInputEditText
            viewModel.account.user.currentWorkPlace=currentWorkPlace.text.toString()
            viewModel.account.user.educationLevel=educationLevel.text.toString()
            viewModel.account.user.dob= dateborn.text.toString()






        }

    }
    private fun generateviewmodel() : SignUpManagerViewModel
    {
        // remember view model tồn tại chung với activity . loginsignup activity

        val viewmodelfactory = MviewmodelProviderFactory(AccountSignUp(User()),requireContext())
         return ViewModelProvider(requireActivity(),viewmodelfactory).get(SignUpManagerViewModel::class.java)  // provide viewmodel cho toi!!
    }
    private fun dotranscation(fragment: Fragment){
        val transcation = childFragmentManager.beginTransaction()
        transcation.replace(R.id.sigup_container,fragment)
        transcation.addToBackStack(null)
        transcation.commit()
    }
    object managersingleton {
        var listfragment :List<Fragment>
        init {
            listfragment=listOf<Fragment>(UserFillInfoFragment(),UserFillInfoFragment2(),UserFillInfoFragment3())
        }
    }
    object  youaresingleton : YouAreFragment()

}