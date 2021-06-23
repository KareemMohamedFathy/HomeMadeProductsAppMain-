package com.homemadeproductsapp.PastOrders.Listener

import com.homemadeproductsapp.DB.Order

interface OrderClickListener {
    fun checkOrderDetails(order: Order)
}