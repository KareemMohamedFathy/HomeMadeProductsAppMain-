package com.homemadeproductsapp.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.R


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottom_navigation)


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    Log.d("lolol","lol2")
                    true
                }
                R.id.page_2 -> {
                    Log.d("lolol","lol")
                    val intent = Intent(this@HomeActivity, MyStoreActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}