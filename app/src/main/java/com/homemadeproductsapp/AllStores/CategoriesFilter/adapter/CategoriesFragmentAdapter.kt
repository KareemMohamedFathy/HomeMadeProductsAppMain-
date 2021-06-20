package com.homemadeproductsapp.AllStores.CategoriesFilter.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.homemadeproductsapp.AllStores.CategoriesFilter.MainCategoriesFragment
import com.homemadeproductsapp.AllStores.CategoriesFilter.SubCategoriesFragment

class CategoriesFragmentAdapter(fm:FragmentActivity):FragmentStateAdapter(fm) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainCategoriesFragment()
            else -> SubCategoriesFragment()
        }

    }
}