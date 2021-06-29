package com.homemadeproductsapp.Search

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.homemadeproductsapp.R
import com.homemadeproductsapp.Search.adapter.SearchResultsAdapter

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var viewPager:ViewPager2
    private lateinit var query:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_bar_results)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {

        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH == intent.action) {

             query = intent.getStringExtra(SearchManager.QUERY).toString()
            Log.d("kuso", query.toString())
            handleTabs()


        }
    }


    private fun handleTabs() {

        val pageAdapter = SearchResultsAdapter(this)


        var first = true
        pageAdapter.addFragment(AllStoresFragment(), "Stores",query)
        pageAdapter.addFragment(AlltemsFragment(), "Items",query)

        viewPager = findViewById(R.id.viewPager2)

        viewPager.adapter = pageAdapter


        val tabs1 = findViewById<TabLayout>(R.id.tabs)
        TabLayoutMediator(tabs1, viewPager) { tab, position ->
            tab.text = pageAdapter.getPageTitle(position)

        }.attach()

    }

}