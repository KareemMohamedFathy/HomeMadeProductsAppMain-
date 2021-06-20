package com.homemadeproductsapp.AllStores.CategoriesFilter

import AllStoresFragmentAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.viewpager2.widget.ViewPager2
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.AllStores.CategoriesFilter.adapter.CategoriesFragmentAdapter
import com.homemadeproductsapp.AllStores.CategoriesFilter.listeners.SubCategoriesListener
import com.homemadeproductsapp.AppConst
import com.homemadeproductsapp.DB.Local.StoreSession
import com.homemadeproductsapp.R
import com.homemadeproductsapp.profile.adapter.ProfileAdapter
import com.mindorks.notesapp.data.local.pref.PrefConstant

class CategoryFiltersActivity : AppCompatActivity(),onMoveGo, SubCategoriesListener,backtoMain,CategoryBoth {
    private lateinit var viewPager:ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_filters)
        bindViews()
    }

    private fun bindViews() {
        viewPager=findViewById(R.id.viewPager2)
        val pageAdapter= CategoriesFragmentAdapter(this)
        viewPager.adapter=pageAdapter
        viewPager.setUserInputEnabled(false);
    }

    override fun onBack() {
        val intent= Intent(this@CategoryFiltersActivity,AllStoresActivity::class.java)
        startActivity(intent)
    }

    override fun onNext() {
        viewPager.currentItem=1
    }
    private fun setupSharedPreference() {
        StoreSession.init(this)
    }

    override fun displayResults(subcategory: String) {
        setupSharedPreference()
        Log.d("fraud","fraud")
        val intent=Intent(this@CategoryFiltersActivity,SearchResultsActivity::class.java)
        intent.putExtra("subcategory",subcategory)
        intent.putExtra(PrefConstant.CHOOSENCATEGORY,mainCategory)


        startActivity(intent)
    }

    override fun onBackToCat() {
        viewPager.currentItem=0
    }

    override var mainCategory: String=""

}
interface CategoryBoth{
     var mainCategory:String
}