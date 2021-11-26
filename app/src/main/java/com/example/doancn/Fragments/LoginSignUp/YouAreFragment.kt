package com.example.doancn.Fragments.LoginSignUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.doancn.Models.AccountSignUp
import com.example.doancn.R
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.youare_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
open class YouAreFragment
    : Fragment() {

    @Inject
    lateinit var accountSignUp: AccountSignUp
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private val viewModel: SignUpViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.youare_fragment, container, false)
        GlobalScope.launch(Dispatchers.Default) { // tranh ui block khi them data vao account
            toggleGroup = view.group_sigup
            toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.teacher -> {
                            accountSignUp.mrole = 0
                            Log.d("rolenemaydasdsad", accountSignUp.mrole.toString())
                        }
                        R.id.student -> {
                            accountSignUp.mrole = 1
                            Log.d("rolenemaydasdsad", accountSignUp.mrole.toString())
                        }
                    }
                }

            }
        }
        return view

    }
}