package com.homemadeproductsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.Home.HomeActivity
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.profile.ProfileActivity

class OrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        handleBottomNavigationView()
    }
    private fun handleBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setSelectedItemId(R.id.page_3);

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {
                    val intent = Intent(this@OrdersActivity, MyStoreActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_4 -> {
                    val intent = Intent(this@OrdersActivity, AllStoresActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_1 -> {
                    val intent = Intent(this@OrdersActivity, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_5 -> {
                    val intent = Intent(this@OrdersActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }

        }
    }
}