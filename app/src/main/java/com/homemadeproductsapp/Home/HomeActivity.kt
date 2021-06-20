package com.homemadeproductsapp.Home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.OrdersActivity
import com.homemadeproductsapp.profile.ProfileActivity
import com.homemadeproductsapp.R


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        handleBottomNavigationView();

    }

    private fun handleBottomNavigationView() {
        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setSelectedItemId(R.id.page_1);

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {

                    val intent = Intent(this@HomeActivity, MyStoreActivity::class.java)
                    startActivity(intent)

                    true
                }
                R.id.page_3 -> {
                    val intent = Intent(this@HomeActivity, OrdersActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_4 -> {
                    val intent = Intent(this@HomeActivity, AllStoresActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_5 -> {
                    val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }

        }
    }
}