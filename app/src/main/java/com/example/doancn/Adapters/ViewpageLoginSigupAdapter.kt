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
    override fun getCount(): Int {
        return BlockDataForThisAdapter.listframent.size
    }

    override fun getItem(position: Int): Fragment {
        return BlockDataForThisAdapter.listframent[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return BlockDataForThisAdapter.listname[position]
    }
}
 object BlockDataForThisAdapter {
      var listname : Array<String>
      var listframent : Array<Fragment>
     init {
            listname = arrayOf("Đăng Nhập", "Đăng Ký")
         listframent = arrayOf(LoginFragment(), SigupFragment())
     }
}