package com.homemadeproductsapp.profile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.homemadeproductsapp.profile.ProfileFragment
import com.homemadeproductsapp.profile.UpdateProfileFragment
import com.homemadeproductsapp.profile.UpdateStoreFragment

class ProfileAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
    return when(position) {
        1 -> UpdateProfileFragment()
        2 ->UpdateStoreFragment()
        else -> ProfileFragment()

    }
    }

}