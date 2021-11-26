package com.example.doancn.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.doancn.Fragments.LoginSignUp.LoginFragment
import com.example.doancn.Fragments.LoginSignUp.SigupFragment

class ViewpageLoginSigupAdapter(support: FragmentManager) : FragmentStatePagerAdapter(
    support,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    val listname = arrayOf("Đăng Nhập", "Đăng Ký")
    val listframent = arrayOf(LoginFragment(), SigupFragment())

    override fun getCount(): Int {
        return listframent.size
    }

    override fun getItem(position: Int): Fragment {

        return listframent[position]

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return listname[position]
    }
}
