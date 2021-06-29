package com.homemadeproductsapp.DB


import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.time.LocalDate
import java.time.Month
import java.util.*


@IgnoreExtraProperties
data class Order(
    var order_id: String? = "",
    var store_id: String? = "",
    var cart: Cart,
    var user_id:String?="",
    var date:String?="",
    var order_status:String=""



    ) : Serializable