package com.homemadeproductsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.Home.HomeActivity
import com.homemadeproductsapp.MyStore.MyStoreActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        handleBottomNavigationView()
    }
    private fun handleBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setSelectedItemId(R.id.page_5);

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {
                    val intent = Intent(this@ProfileActivity, MyStoreActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_4 -> {
                    val intent = Intent(this@ProfileActivity, AllStoresActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_1 -> {
                    val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page_3 -> {
                    val intent = Intent(this@ProfileActivity, OrdersActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
        }
}