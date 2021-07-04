package com.homemadeproductsapp.Search

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.Home.HomeActivity
import com.homemadeproductsapp.R
import com.homemadeproductsapp.Search.adapter.SearchResultsAdapter

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var viewPager:ViewPager2
    private lateinit var query:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_bar_results)

        handleIntent(intent)
setupToolbarText()
    }

    override fun onNewIntent(intent: Intent) {

        super.onNewIntent(intent)
        handleIntent(intent)
    }
    private fun setupToolbarText() {
        if (supportActionBar != null) {
            Log.d("kuso","kuso")

            getSupportActionBar()!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar()!!.setCustomView(R.layout.actionbarallstore);

            getSupportActionBar()!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar()!!.setCustomView(R.layout.actionbar);
            val view = supportActionBar!!.customView
            var textViewTitle: TextView =view.findViewById(R.id.action_bar_title)
            textViewTitle.text="Search Results"

            var back: ImageView =view.findViewById(R.id.action_bar_Image)
            back.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                        val intent: Intent = Intent(
                                this@SearchResultsActivity,
                                HomeActivity::class.java
                        )
                        finish()
                        startActivity(intent)
                    }
                })




//            var textViewTitle: TextView =view.findViewById(R.id.action_bar_title)

        }
    }


    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE)
                        .saveRecentQuery(query, null)
            }
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