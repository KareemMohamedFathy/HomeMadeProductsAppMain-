package com.homemadeproductsapp.MyStore.Adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyStoreFragmentAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()


    fun addFragment(fragment: Fragment, title: String, storeIdExists: String): Fragment {


        mFragmentList.add(fragment)
        mFragmentTitleList.add("" + title + "")
        val args = Bundle()
        args.putString("Category", title)
        args.putString("store_id", storeIdExists)

        fragment.setArguments(args);

        return fragment
    }

    fun addTimeLineFragment(
        fragment: Fragment,
        title: String,
        storename: String,
        storeimagePath: String, storeIdExists: String
    ): Fragment {


        mFragmentList.add(fragment)
        mFragmentTitleList.add("" + title + "")
        val args = Bundle()
        args.putString("StoreName", storename)
        args.putString("storeImagePath", storeimagePath)
        args.putString("store_id", storeIdExists)


        fragment.setArguments(args);

        return fragment
    }

    fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun getPageFragment(position: Int): Fragment? {
        return mFragmentList[position]
    }


    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            else -> mFragmentList.get(position)

        }
    }
}