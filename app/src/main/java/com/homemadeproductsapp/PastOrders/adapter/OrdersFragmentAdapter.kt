package com.homemadeproductsapp.PastOrders.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.homemadeproductsapp.PastOrders.AllOrdersFragment
import com.homemadeproductsapp.PastOrders.OrderFragment

class OrdersFragmentAdapter(fm:FragmentActivity):FragmentStateAdapter(fm) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->AllOrdersFragment()
            else->OrderFragment()
        }
    }

}