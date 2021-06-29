package com.homemadeproductsapp.MyStore.AcceptOrders.Listener

import com.homemadeproductsapp.DB.Order

interface OrderAcceptClickListener {
    fun checkOrderDetails(order: Order)

}