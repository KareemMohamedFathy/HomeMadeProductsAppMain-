package com.homemadeproductsapp.Search.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class SearchResultsAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm)
{
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String,query:String): Fragment {


        mFragmentList.add(fragment)
        mFragmentTitleList.add("" + title + "")
        val args = Bundle()
        args.putString("query", query)
        fragment.setArguments(args);
        return fragment
    }
    fun addTimeLineFragment(fragment: Fragment,title: String, storename: String,storeimagePath:String): Fragment {


        mFragmentList.add(fragment)
        mFragmentTitleList.add("" + title + "")
        val args = Bundle()
        args.putString("StoreName", storename)
        args.putString("storeImagePath", storeimagePath)

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
        return  mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            else->mFragmentList.get(position)
        }
    }
}