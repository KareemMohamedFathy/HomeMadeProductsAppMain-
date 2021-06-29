package com.homemadeproductsapp.PastOrders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homemadeproductsapp.AllStores.AllStoresActivity
import com.homemadeproductsapp.DB.Order
import com.homemadeproductsapp.Home.HomeActivity
import com.homemadeproductsapp.MyStore.MyStoreActivity
import com.homemadeproductsapp.PastOrders.Listener.OrderClickListener
import com.homemadeproductsapp.PastOrders.adapter.OrdersFragmentAdapter
import com.homemadeproductsapp.R
import com.homemadeproductsapp.profile.ProfileActivity

class OrdersActivity : AppCompatActivity(),OrderClickListener,dataCommunication,BackToAllOrders {
    private lateinit var viewPager2: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        handleBottomNavigationView()
        bindViews()
    }

    private fun bindViews() {
        viewPager2=findViewById(R.id.viewPager2)
        val adapter=OrdersFragmentAdapter(this)
        viewPager2.adapter=adapter
        viewPager2.setUserInputEnabled(false)
    }
    private fun handleBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setSelectedItemId(R.id.page_3);

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {
                    val intent = Intent(this@OrdersActivity, MyStoreActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_4 -> {
                    val intent = Intent(this@OrdersActivity, AllStoresActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_1 -> {
                    val intent = Intent(this@OrdersActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                R.id.page_5 -> {
                    val intent = Intent(this@OrdersActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }

                else -> false
            }

        }
    }

    override fun checkOrderDetails(order: Order) {
        viewPager2.currentItem=1
        chosenOrder=order
    }

    override var chosenOrder: Order?=null
    override fun backToHome() {
     viewPager2.currentItem=0
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


}

interface dataCommunication{
    var chosenOrder:Order?
}